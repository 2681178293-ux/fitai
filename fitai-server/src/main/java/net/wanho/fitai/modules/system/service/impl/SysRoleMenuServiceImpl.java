package net.wanho.fitai.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import net.wanho.fitai.modules.system.mapper.SysRoleMenuMapper;
import net.wanho.fitai.modules.system.model.po.SysRoleMenu;
import net.wanho.fitai.modules.system.service.SysRoleMenuServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SysRoleMenuServiceImpl extends MPJBaseServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuServiceI {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceByRoleId(Long roleId, List<Long> menuIds) {
        this.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        List<SysRoleMenu> list = menuIds.stream().map(menuId -> {
            SysRoleMenu item = new SysRoleMenu();
            item.setRoleId(roleId);
            item.setMenuId(menuId);
            return item;
        }).toList();
        this.saveBatch(list);
    }

    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return this.list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId))
                .stream().map(SysRoleMenu::getMenuId).toList();
    }
}
