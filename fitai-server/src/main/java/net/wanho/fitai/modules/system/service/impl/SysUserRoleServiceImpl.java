package net.wanho.fitai.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import net.wanho.fitai.modules.system.mapper.SysUserRoleMapper;
import net.wanho.fitai.modules.system.model.po.SysUserRole;
import net.wanho.fitai.modules.system.service.SysUserRoleServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SysUserRoleServiceImpl extends MPJBaseServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleServiceI {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceByUserId(Long userId, List<Long> roleIds) {
        this.remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        List<SysUserRole> list = roleIds.stream().map(roleId -> {
            SysUserRole item = new SysUserRole();
            item.setUserId(userId);
            item.setRoleId(roleId);
            return item;
        }).toList();
        this.saveBatch(list);
    }

    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return this.list(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(SysUserRole::getRoleId).toList();
    }
}
