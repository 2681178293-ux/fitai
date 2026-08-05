package net.wanho.fitai.modules.system.service;

import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.po.SysRoleDept;

import java.util.List;

public interface SysRoleDeptServiceI extends MPJBaseService<SysRoleDept> {
    void replaceByRoleId(Long roleId, List<Long> deptIds);

    List<Long> listDeptIdsByRoleId(Long roleId);
}
