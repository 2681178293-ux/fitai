package net.wanho.fitai.modules.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import net.wanho.fitai.modules.mall.model.dto.*;
import net.wanho.fitai.modules.mall.model.vo.AdminProductVO;
import net.wanho.fitai.modules.mall.model.vo.AdminSkuVO;
import net.wanho.fitai.modules.mall.model.vo.PrdAttrVO;

import java.util.List;

public interface AdminProductServiceI {
    IPage<AdminProductVO> findByPage(ProductQueryDTO queryDto);


    AdminProductVO getDetailById(Long spuId);

    void addProduct(ProductAddDTO addDTO);

    void updateProduct(Long spuId, ProductUpdateDTO updateDto);

    List<AdminSkuVO> getSkuList(Long spuId);

    List<PrdAttrVO> getSaleAttr(Long spuId);

    void addSku(Long spuId, SkuAddDTO addDto);

    void updateSku(Long skuId, SkuUpdateDTO updateDTO);

    void deleteSku(Long skuId);
}
