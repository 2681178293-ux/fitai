package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAttrAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAttrQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAttrUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdCategoryAttr;
import net.wanho.fitai.modules.mall.model.vo.PrdCategoryAttrVO;

public interface PrdCategoryAttrServiceI extends MPJBaseService<PrdCategoryAttr> {
    IPage<PrdCategoryAttrVO> findByPage(PrdCategoryAttrQueryDTO queryDto);

    void add(PrdCategoryAttrAddDTO addDto);

    void update(PrdCategoryAttrUpdateDTO updateDto);

    void delete(String id);

    PrdCategoryAttrVO findById(Long id);

}
