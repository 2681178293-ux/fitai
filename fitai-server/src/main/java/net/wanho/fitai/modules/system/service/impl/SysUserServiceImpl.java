package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.auth.constant.CommonConstant;
import net.wanho.fitai.modules.system.mapper.SysMenuMapper;
import net.wanho.fitai.modules.system.mapper.SysRoleMapper;
import net.wanho.fitai.modules.system.mapper.SysUserMapper;
import net.wanho.fitai.modules.system.model.dto.SysUserAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysUserQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysUserUpdateDTO;
import net.wanho.fitai.modules.system.model.po.*;
import net.wanho.fitai.modules.system.model.vo.SysUserVO;
import net.wanho.fitai.modules.system.service.SysUserRoleServiceI;
import net.wanho.fitai.modules.system.service.SysUserServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends MPJBaseServiceImpl<SysUserMapper, SysUser> implements SysUserServiceI {

    private final Converter converter;
    private final SysUserRoleServiceI sysUserRoleService;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMapper sysRoleMapper;

    @Override
    public SysUser getByUserName(String userName) {
        SysUser sysUser = this.getOne(new LambdaQueryWrapper<SysUser>().eq(StrUtil.isNotEmpty(userName), SysUser::getUserName, userName));
        return sysUser;
    }

    @Override
    public List<String> getPermsByUserId(Long userId) {

        // select m.perms from sys_menu m
        // inner join sys_role_menu rm on m.menu_id = rm.menu_id
        // inner join sys_user_role ur on rm.role_id = ur.role_id
        // where ur.user_id=2

        List<SysMenu> sysMenus = sysMenuMapper.selectJoinList(SysMenu.class,
                new MPJLambdaWrapper<SysMenu>()
                        .select(SysMenu::getPerms)
                        .innerJoin(SysRoleMenu.class, SysRoleMenu::getMenuId, SysMenu::getMenuId)
                        .innerJoin(SysUserRole.class, SysUserRole::getRoleId, SysRoleMenu::getRoleId)
                        .eq(SysUserRole::getUserId, userId)
                        .eq(SysRole::getStatus, CommonConstant.STATUS_NORMAL)
        );

        return sysMenus.stream().map(SysMenu::getPerms).filter(StrUtil::isNotEmpty).distinct().toList();
    }

    @Override
    public List<String> getRoleKeysByUserId(Long userId) {

        // select r.role_key from sys_role r left join sys_user_role ur on r.role_id = ur.role_id where ur.user_id = #{userId}
        List<SysRole> sysRoles = sysRoleMapper.selectJoinList(SysRole.class,
                new MPJLambdaWrapper<SysRole>()
                        .select(SysRole::getRoleKey)
                        .innerJoin(SysUserRole.class, SysUserRole::getRoleId, SysRole::getRoleId)
                        .eq(SysUserRole::getUserId, userId)
                        .eq(SysRole::getStatus, CommonConstant.STATUS_NORMAL)
        );
        return sysRoles.stream().map(SysRole::getRoleKey).filter(StrUtil::isNotEmpty).toList();
    }

    @Override
    public List<String> getAllPerms() {
        List<SysMenu> sysMenuList = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .select(SysMenu::getPerms)
                        .eq(SysMenu::getStatus, CommonConstant.STATUS_NORMAL)
                        .isNotNull(SysMenu::getPerms)
                        .ne(SysMenu::getPerms, "")
        );
        return sysMenuList.stream().map(SysMenu::getPerms).filter(StrUtil::isNotEmpty).distinct().toList();
    }

    @Override
    public List<String> getAllRoleKeys() {
        return sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .select(SysRole::getRoleKey)
                        .eq(SysRole::getStatus, CommonConstant.STATUS_NORMAL)
        ).stream().map(SysRole::getRoleKey).filter(StrUtil::isNotEmpty).toList();
    }

    @Override
    public IPage<SysUserVO> findByPage(SysUserQueryDTO queryDto) {
        IPage<SysUser> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getUserName()), SysUser::getUserName, queryDto.getUserName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getNickName()), SysUser::getNickName, queryDto.getNickName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPhonenumber()), SysUser::getPhonenumber, queryDto.getPhonenumber());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysUser::getStatus, queryDto.getStatus());
        wrapper.eq(queryDto.getDeptId() != null, SysUser::getDeptId, queryDto.getDeptId());
        IPage<SysUser> result = this.page(page, wrapper);
        return result.convert(item -> {
            SysUserVO vo = converter.convert(item, SysUserVO.class);
            vo.setRoleIds(sysUserRoleService.listRoleIdsByUserId(item.getUserId()));
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SysUserAddDTO addDto) {
        SysUser entity = converter.convert(addDto, SysUser.class);
        this.save(entity);
        sysUserRoleService.replaceByUserId(entity.getUserId(), addDto.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUserUpdateDTO updateDto) {
        SysUser entity = converter.convert(updateDto, SysUser.class);
        this.updateById(entity);
        if (updateDto.getRoleIds() != null) {
            sysUserRoleService.replaceByUserId(updateDto.getUserId(), updateDto.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        String[] ids = id.split(",");
        for (String userId : ids) {
            sysUserRoleService.replaceByUserId(Long.valueOf(userId), List.of());
        }
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysUserVO findById(Long userId) {
        SysUser entity = this.getById(userId);
        SysUserVO vo = converter.convert(entity, SysUserVO.class);
        if (vo != null) {
            vo.setRoleIds(sysUserRoleService.listRoleIdsByUserId(userId));
        }
        return vo;
    }
}
