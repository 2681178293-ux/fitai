package net.wanho.fitai.modules.system.service;

import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysDeptAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDeptQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDeptUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysDept;
import net.wanho.fitai.modules.system.model.vo.SysDeptVO;

import java.util.List;

public interface SysDeptServiceI extends MPJBaseService<SysDept> {

    void add(SysDeptAddDTO addDto);

    void update(SysDeptUpdateDTO updateDto);

    void delete(String id);

    SysDeptVO findById(Long id);

    List<SysDeptVO> listTree(SysDeptQueryDTO queryDto);

}
