package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysMenuAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysMenuQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysMenuUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysMenu;
import net.wanho.fitai.modules.system.model.vo.RouterVO;
import net.wanho.fitai.modules.system.model.vo.SysMenuVO;

import java.util.List;

public interface SysMenuServiceI extends MPJBaseService<SysMenu> {

    void add(SysMenuAddDTO addDto);

    void update(SysMenuUpdateDTO updateDto);

    void delete(String id);

    SysMenuVO findById(Long id);

    List<RouterVO> buildRoutersForCurrentUser();

    List<SysMenuVO> listTree(SysMenuQueryDTO queryDto);
}
