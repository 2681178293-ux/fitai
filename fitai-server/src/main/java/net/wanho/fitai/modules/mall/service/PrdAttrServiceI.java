package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdAttr;
import net.wanho.fitai.modules.mall.model.vo.PrdAttrVO;

public interface PrdAttrServiceI extends MPJBaseService<PrdAttr> {
    IPage<PrdAttrVO> findByPage(PrdAttrQueryDTO queryDto);

    void add(PrdAttrAddDTO addDto);

    void update(PrdAttrUpdateDTO updateDto);

    void delete(String id);

    PrdAttrVO findById(Long id);

}
