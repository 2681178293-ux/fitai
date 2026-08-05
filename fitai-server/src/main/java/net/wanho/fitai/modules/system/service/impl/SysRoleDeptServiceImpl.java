package net.wanho.fitai.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import net.wanho.fitai.modules.system.mapper.SysRoleDeptMapper;
import net.wanho.fitai.modules.system.model.po.SysRoleDept;
import net.wanho.fitai.modules.system.service.SysRoleDeptServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SysRoleDeptServiceImpl extends MPJBaseServiceImpl<SysRoleDeptMapper, SysRoleDept> implements SysRoleDeptServiceI {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceByRoleId(Long roleId, List<Long> deptIds) {
        this.remove(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, roleId));
        if (deptIds == null || deptIds.isEmpty()) {
            return;
        }
        List<SysRoleDept> list = deptIds.stream().map(deptId -> {
            SysRoleDept item = new SysRoleDept();
            item.setRoleId(roleId);
            item.setDeptId(deptId);
            return item;
        }).toList();
        this.saveBatch(list);
    }

    @Override
    public List<Long> listDeptIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return this.list(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, roleId))
                .stream().map(SysRoleDept::getDeptId).toList();
    }
}
