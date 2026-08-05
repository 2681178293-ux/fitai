package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysDictDataAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictDataQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictDataUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysDictData;
import net.wanho.fitai.modules.system.model.vo.SysDictDataVO;

public interface SysDictDataServiceI extends MPJBaseService<SysDictData> {
    IPage<SysDictDataVO> findByPage(SysDictDataQueryDTO queryDto);

    void add(SysDictDataAddDTO addDto);

    void update(SysDictDataUpdateDTO updateDto);

    void delete(String id);

    SysDictDataVO findById(Long id);
}
