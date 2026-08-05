package net.wanho.fitai.modules.system.service;

import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.po.SysUserRole;

import java.util.List;

public interface SysUserRoleServiceI extends MPJBaseService<SysUserRole> {
    void replaceByUserId(Long userId, List<Long> roleIds);

    List<Long> listRoleIdsByUserId(Long userId);
}
