package net.wanho.fitai.modules.system.service;

import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.po.SysRoleMenu;

import java.util.List;

public interface SysRoleMenuServiceI extends MPJBaseService<SysRoleMenu> {
    void replaceByRoleId(Long roleId, List<Long> menuIds);

    List<Long> listMenuIdsByRoleId(Long roleId);
}
