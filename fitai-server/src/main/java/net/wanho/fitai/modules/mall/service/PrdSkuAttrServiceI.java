package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAttrAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAttrQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAttrUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdSkuAttr;
import net.wanho.fitai.modules.mall.model.vo.PrdSkuAttrVO;

public interface PrdSkuAttrServiceI extends MPJBaseService<PrdSkuAttr> {
    IPage<PrdSkuAttrVO> findByPage(PrdSkuAttrQueryDTO queryDto);

    void add(PrdSkuAttrAddDTO addDto);

    void update(PrdSkuAttrUpdateDTO updateDto);

    void delete(String id);

    PrdSkuAttrVO findById(Long id);

}
