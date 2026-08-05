package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysRoleMapper;
import net.wanho.fitai.modules.system.model.dto.SysRoleAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysRoleQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysRoleUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysRole;
import net.wanho.fitai.modules.system.model.vo.SysRoleVO;
import net.wanho.fitai.modules.system.service.SysRoleDeptServiceI;
import net.wanho.fitai.modules.system.service.SysRoleMenuServiceI;
import net.wanho.fitai.modules.system.service.SysRoleServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends MPJBaseServiceImpl<SysRoleMapper, SysRole> implements SysRoleServiceI {

    private final Converter converter;
    private final SysRoleMenuServiceI sysRoleMenuService;
    private final SysRoleDeptServiceI sysRoleDeptService;

    @Override
    public IPage<SysRoleVO> findByPage(SysRoleQueryDTO queryDto) {
        IPage<SysRole> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRoleName()), SysRole::getRoleName, queryDto.getRoleName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRoleKey()), SysRole::getRoleKey, queryDto.getRoleKey());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysRole::getStatus, queryDto.getStatus());
        IPage<SysRole> result = this.page(page, wrapper);
        return result.convert(item -> toVo(item));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SysRoleAddDTO addDto) {
        SysRole entity = converter.convert(addDto, SysRole.class);
        this.save(entity);
        sysRoleMenuService.replaceByRoleId(entity.getRoleId(), addDto.getMenuIds());
        sysRoleDeptService.replaceByRoleId(entity.getRoleId(), addDto.getDeptIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleUpdateDTO updateDto) {
        SysRole entity = converter.convert(updateDto, SysRole.class);
        this.updateById(entity);
        if (updateDto.getMenuIds() != null) {
            sysRoleMenuService.replaceByRoleId(updateDto.getRoleId(), updateDto.getMenuIds());
        }
        if (updateDto.getDeptIds() != null) {
            sysRoleDeptService.replaceByRoleId(updateDto.getRoleId(), updateDto.getDeptIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        String[] ids = id.split(",");
        for (String roleId : ids) {
            Long rid = Long.valueOf(roleId);
            sysRoleMenuService.replaceByRoleId(rid, List.of());
            sysRoleDeptService.replaceByRoleId(rid, List.of());
        }
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysRoleVO findById(Long roleId) {
        return toVo(this.getById(roleId));
    }

    private SysRoleVO toVo(SysRole entity) {
        if (entity == null) {
            return null;
        }
        SysRoleVO vo = converter.convert(entity, SysRoleVO.class);
        vo.setMenuIds(sysRoleMenuService.listMenuIdsByRoleId(entity.getRoleId()));
        vo.setDeptIds(sysRoleDeptService.listDeptIdsByRoleId(entity.getRoleId()));
        return vo;
    }
}
