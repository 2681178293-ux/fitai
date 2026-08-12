package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysRoleAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysRoleQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysRoleUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysRole;
import net.wanho.fitai.modules.system.model.vo.SysRoleVO;

import java.util.List;

public interface SysRoleServiceI extends MPJBaseService<SysRole> {
    IPage<SysRoleVO> findByPage(SysRoleQueryDTO queryDto);

    void add(SysRoleAddDTO addDto);

    void update(SysRoleUpdateDTO updateDto);

    void delete(String id);

    SysRoleVO findById(Long roleId);

    List<SysRoleVO> listAll();
}
