package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysDictTypeAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictTypeQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictTypeUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysDictType;
import net.wanho.fitai.modules.system.model.vo.SysDictTypeVO;

public interface SysDictTypeServiceI extends MPJBaseService<SysDictType> {
    IPage<SysDictTypeVO> findByPage(SysDictTypeQueryDTO queryDto);

    void add(SysDictTypeAddDTO addDto);

    void update(SysDictTypeUpdateDTO updateDto);

    void delete(String id);

    SysDictTypeVO findById(Long id);
}
