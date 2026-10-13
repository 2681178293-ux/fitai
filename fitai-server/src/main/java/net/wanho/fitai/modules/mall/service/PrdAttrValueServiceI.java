package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrValueAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrValueQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrValueUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdAttrValue;
import net.wanho.fitai.modules.mall.model.vo.PrdAttrValueVO;

public interface PrdAttrValueServiceI extends MPJBaseService<PrdAttrValue> {
    IPage<PrdAttrValueVO> findByPage(PrdAttrValueQueryDTO queryDto);

    void add(PrdAttrValueAddDTO addDto);

    void update(PrdAttrValueUpdateDTO updateDto);

    void delete(String id);

    PrdAttrValueVO findById(Long id);

}
