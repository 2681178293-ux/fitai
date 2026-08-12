package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysUserAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysUserQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysUserUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysUser;
import net.wanho.fitai.modules.system.model.vo.SysUserVO;

import java.util.List;

public interface SysUserServiceI extends MPJBaseService<SysUser> {


    /** 按用户名查询（认证用，返回 PO） */
    SysUser getByUserName(String userName);

    /** 用户权限标识列表 */
    List<String> getPermsByUserId(Long userId);

    /** 用户角色 key 列表 */
    List<String> getRoleKeysByUserId(Long userId);

    /** 全部权限标识（超级管理员） */
    List<String> getAllPerms();

    /** 全部角色 key（超级管理员） */
    List<String> getAllRoleKeys();

    IPage<SysUserVO> findByPage(SysUserQueryDTO queryDto);

    void add(SysUserAddDTO addDto);

    void update(SysUserUpdateDTO updateDto);

    void delete(String id);

    SysUserVO findById(Long userId);

    void resetPwd(Long userId);
}
