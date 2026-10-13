package net.wanho.fitai.modules.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.exception.CustomException;
import net.wanho.fitai.modules.mall.code.MallCode;
import net.wanho.fitai.modules.mall.mapper.PrdSkuMapper;
import net.wanho.fitai.modules.mall.mapper.PrdSpuDetailMapper;
import net.wanho.fitai.modules.mall.mapper.PrdSpuMapper;
import net.wanho.fitai.modules.mall.mapper.PrdStockMapper;
import net.wanho.fitai.modules.mall.model.dto.*;
import net.wanho.fitai.modules.mall.model.po.*;
import net.wanho.fitai.modules.mall.model.vo.AdminProductVO;
import net.wanho.fitai.modules.mall.model.vo.AdminSkuVO;
import net.wanho.fitai.modules.mall.model.vo.PrdAttrVO;
import net.wanho.fitai.modules.mall.model.vo.SkuSpecItemVO;
import net.wanho.fitai.modules.mall.service.AdminProductServiceI;
import net.wanho.fitai.modules.mall.service.PrdSkuAttrServiceI;
import net.wanho.fitai.modules.mall.service.PrdSpuImageServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductServiceI {

    private final PrdSpuMapper spuMapper;
    private final PrdSpuDetailMapper spuDetailMapper;
    private final PrdSpuImageServiceI spuImageService;
    private final PrdSkuMapper skuMapper;
    private final PrdSkuAttrServiceI skuAttrService;
    private final PrdStockMapper stockMapper;

    @Override
    public IPage<AdminProductVO> findByPage(ProductQueryDTO queryDto) {

        IPage<AdminProductVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        MPJLambdaWrapper<PrdSpu> wrapper = new MPJLambdaWrapper<PrdSpu>()
                .selectAll(PrdSpu.class)
                .selectAs(PrdCategory::getCategoryName, AdminProductVO::getCategoryName)
                .selectAs(PrdBrand::getBrandName, AdminProductVO::getBrandName)
                .selectAs(PrdSpuImage::getImageUrl, AdminProductVO::getCoverUrl)
                .leftJoin(PrdCategory.class, PrdCategory::getCategoryId, PrdSpu::getCategoryId)
                .leftJoin(PrdBrand.class, PrdBrand::getBrandId, PrdSpu::getBrandId)
                .leftJoin(PrdSpuImage.class, on -> on
                        .eq(PrdSpuImage::getSpuId, PrdSpu::getSpuId)
                        .eq(PrdSpuImage::getMainFlag, "1"))
                .like(StrUtil.isNotEmpty(queryDto.getSpuName()), PrdSpu::getSpuName, queryDto.getSpuName())
                .eq(StrUtil.isNotEmpty(queryDto.getStatus()), PrdSpu::getStatus, queryDto.getStatus())
                .like(StrUtil.isNotEmpty(queryDto.getBrandName()), PrdBrand::getBrandName, queryDto.getBrandName())
                .orderByDesc(PrdSpu::getOrderNum)
                .orderByDesc(PrdSpu::getCreateTime);
        return spuMapper.selectJoinPage(page, AdminProductVO.class, wrapper);

    }


    @Override
    public AdminProductVO getDetailById(Long spuId) {
        MPJLambdaWrapper<PrdSpu> wrapper = new MPJLambdaWrapper<PrdSpu>()
                .selectAll(PrdSpu.class)
                .selectAs(PrdCategory::getCategoryName, AdminProductVO::getCategoryName)
                .selectAs(PrdBrand::getBrandName, AdminProductVO::getBrandName)
                .selectAs(PrdSpuDetail::getDescription, AdminProductVO::getDescription)
                .selectAs(PrdSpuDetail::getDetailHtml, AdminProductVO::getDetailHtml)
                .selectAs("t_cover", PrdSpuImage::getImageUrl, AdminProductVO::getCoverUrl)
                .selectCollection("t_album", PrdSpuImage.class, AdminProductVO::getImages, map -> map
                        .result(PrdSpuImage::getImageUrl))
                .leftJoin(PrdCategory.class, PrdCategory::getCategoryId, PrdSpu::getCategoryId)
                .leftJoin(PrdBrand.class, PrdBrand::getBrandId, PrdSpu::getBrandId)
                .leftJoin(PrdSpuDetail.class, PrdSpuDetail::getSpuId, PrdSpu::getSpuId)
                .leftJoin(PrdSpuImage.class, "t_album", PrdSpuImage::getSpuId, PrdSpu::getSpuId, on -> on
                        .eq(PrdSpuImage::getMainFlag, "0"))
                .leftJoin(PrdSpuImage.class, "t_cover", on -> on
                        .eq(PrdSpuImage::getSpuId, PrdSpu::getSpuId)
                        .eq(PrdSpuImage::getMainFlag, "1"))
                .eq(PrdSpu::getSpuId, spuId)
                .orderByDesc(PrdSpu::getOrderNum)
                .orderByDesc(PrdSpu::getCreateTime);
        return spuMapper.selectJoinOne(AdminProductVO.class, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(ProductAddDTO addDTO) {
        PrdSpu prdSpu = new PrdSpu();
        prdSpu.setSpuSn("SPU-" + System.currentTimeMillis());
        prdSpu.setSpuName(addDTO.getSpuName());
        prdSpu.setCategoryId(addDTO.getCategoryId());
        prdSpu.setBrandId(addDTO.getBrandId());
        spuMapper.insert(prdSpu);

        addPrdSpuDetail(prdSpu.getSpuId(), addDTO.getDescription(), addDTO.getDetailHtml());
        addPrdSpuImage(prdSpu.getSpuId(), addDTO.getCoverUrl(), addDTO.getImages());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long spuId, ProductUpdateDTO updateDto) {
        PrdSpu prdSpu = new PrdSpu();
        prdSpu.setSpuId(spuId);
        prdSpu.setSpuName(updateDto.getSpuName());
        prdSpu.setCategoryId(updateDto.getCategoryId());
        prdSpu.setBrandId(updateDto.getBrandId());
        spuMapper.updateById(prdSpu);

        spuImageService.remove(new LambdaQueryWrapper<PrdSpuImage>().eq(PrdSpuImage::getSpuId, spuId));
        addPrdSpuImage(spuId, updateDto.getCoverUrl(), updateDto.getImages());

        spuDetailMapper.delete(new LambdaQueryWrapper<PrdSpuDetail>().eq(PrdSpuDetail::getSpuId, spuId));
        addPrdSpuDetail(spuId, updateDto.getDescription(), updateDto.getDetailHtml());

    }

    //根据spuId获取sku列表
    @Override
    public List<AdminSkuVO> getSkuList(Long spuId) {

        MPJLambdaWrapper<PrdSku> wrapper = new MPJLambdaWrapper<PrdSku>();
        wrapper.selectAll(PrdSku.class);
        wrapper.selectAs(PrdStock::getQuantity, AdminSkuVO::getQuantity);
        wrapper.selectAs(PrdSpuImage::getImageUrl, AdminSkuVO::getCoverUrl);
        wrapper.selectCollection(AdminSkuVO::getSpecs, map -> map
                .id(PrdSku::getSkuId, SkuSpecItemVO::getSkuId)
                .result(PrdAttr::getAttrId, SkuSpecItemVO::getAttrId)
                .result(PrdAttr::getAttrName, SkuSpecItemVO::getAttrName)
                .result(PrdAttrValue::getValueId, SkuSpecItemVO::getValueId)
                .result(PrdAttrValue::getAttrValue, SkuSpecItemVO::getAttrValue)

        );
        wrapper.leftJoin(PrdStock.class, PrdStock::getSkuId, PrdSku::getSkuId);
        wrapper.leftJoin(PrdSpuImage.class, on -> on
                .eq(PrdSpuImage::getSpuId, PrdSku::getSpuId)
                .eq(PrdSpuImage::getMainFlag, "1")
        );
        wrapper.leftJoin(PrdSkuAttr.class, PrdSkuAttr::getSkuId, PrdSku::getSkuId);
        wrapper.leftJoin(PrdAttr.class, PrdAttr::getAttrId, PrdSkuAttr::getAttrId);
        wrapper.leftJoin(PrdAttrValue.class, PrdAttrValue::getValueId, PrdSkuAttr::getValueId);
        wrapper.eq(PrdSku::getSpuId, spuId);
        return skuMapper.selectJoinList(AdminSkuVO.class, wrapper);

    }

    //根据spuId获取销售属性列表
    @Override
    public List<PrdAttrVO> getSaleAttr(Long spuId) {
        //  prd_spu prd_category_attr  prd_attr prd_attr_value

        MPJLambdaWrapper<PrdSpu> wrapper = new MPJLambdaWrapper<PrdSpu>();
        wrapper.selectAll(PrdAttr.class);
        wrapper.selectCollection(PrdAttrValue.class, PrdAttrVO::getValues);

        wrapper.leftJoin(PrdCategoryAttr.class, PrdCategoryAttr::getCategoryId, PrdSpu::getCategoryId);
        wrapper.leftJoin(PrdAttr.class, PrdAttr::getAttrId, PrdCategoryAttr::getAttrId);
        wrapper.leftJoin(PrdAttrValue.class, PrdAttrValue::getAttrId, PrdAttr::getAttrId);

        wrapper.eq(PrdSpu::getSpuId, spuId);
        wrapper.orderByDesc(PrdAttr::getOrderNum);
        wrapper.orderByDesc(PrdAttrValue::getOrderNum);
        return spuMapper.selectJoinList(PrdAttrVO.class, wrapper);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSku(Long spuId, SkuAddDTO addDto) {

        // 通过spuId判断PrdSku是否存在相同的sku
        PrdSku exists = skuMapper.selectOne(new LambdaQueryWrapper<PrdSku>()
                .eq(PrdSku::getSpuId, spuId)
                .eq(PrdSku::getSkuName, addDto.getSkuName())
                .eq(PrdSku::getSkuSpecs, addDto.getSkuSpecs()));
        if (ObjectUtil.isNotNull(exists)) {
            throw new CustomException(MallCode.SKU_EXISTS);
        }

        // prd_sku
        PrdSku prdSku = new PrdSku();
        prdSku.setSpuId(spuId);
        prdSku.setSkuSn("SKU-" + System.currentTimeMillis());
        prdSku.setSkuName(addDto.getSkuName());
        prdSku.setSkuSpecs(addDto.getSkuSpecs());
        prdSku.setPrice(addDto.getPrice());
        skuMapper.insert(prdSku);

        Long skuId = prdSku.getSkuId();

        // prd_sku_attr
        skuAttrService.remove(new LambdaQueryWrapper<PrdSkuAttr>().eq(PrdSkuAttr::getSkuId, skuId));
        List<PrdSkuAttr> prdSkuAttrList = addDto.getSpecs().stream().map(item -> {
            PrdSkuAttr prdSkuAttr = new PrdSkuAttr();
            prdSkuAttr.setSkuId(skuId);
            prdSkuAttr.setAttrId(item.getAttrId());
            prdSkuAttr.setValueId(item.getValueId());
            return prdSkuAttr;
        }).toList();
        skuAttrService.saveBatch(prdSkuAttrList);

        // prd_stock     如果前端传了多个skuId，就调用多次addSku方法
        PrdStock one = stockMapper.selectOne(new LambdaQueryWrapper<PrdStock>()
        .eq(PrdStock::getSkuId, skuId)
        .eq(PrdStock::getWarehouseCode, "DEFAULT"));
        if (ObjectUtil.isNull(one)) {
            // insert
            PrdStock prdStock = new PrdStock();
            prdStock.setSkuId(skuId);
            prdStock.setWarehouseCode("DEFAULT");
            prdStock.setQuantity(addDto.getQuantity());
            stockMapper.insert(prdStock);
        } else {              
            // update
            PrdStock prdStock = new PrdStock();
            prdStock.setSkuId(one.getSkuId());
            prdStock.setWarehouseCode(one.getWarehouseCode());
            prdStock.setQuantity(addDto.getQuantity() + one.getLockedQty());
            stockMapper.updateById(prdStock);
        }

        // prd_spu
        List<Long> skuIds = skuMapper.selectList(new LambdaQueryWrapper<PrdSku>()
                .eq(PrdSku::getSpuId, spuId))
                .stream().map(item -> item.getSkuId())
                .toList();
        int sum = stockMapper.selectList(new LambdaQueryWrapper<PrdStock>()
                        .in(PrdStock::getSkuId, skuIds))
                .stream()
                .mapToInt(PrdStock::getQuantity).sum();
        PrdSpu prdSkuStock = new PrdSpu();
        prdSkuStock.setSpuId(spuId);

        List<PrdSku> prdSkus = skuMapper.selectList(new LambdaQueryWrapper<PrdSku>()
                .in(PrdSku::getSpuId, spuId));
        BigDecimal min = prdSkus.stream().map(PrdSku::getPrice)
                .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal max = prdSkus.stream().map(PrdSku::getPrice)
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        prdSkuStock.setMinPrice(min);
        prdSkuStock.setMaxPrice(max);
        prdSkuStock.setTotalStock(sum);
        //因为已经存在了，所以直接更新补全其他属性
        spuMapper.updateById(prdSkuStock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSku(Long skuId, SkuUpdateDTO updateDTO) {

        // prd_sku
        PrdSku prdSku = new PrdSku();
        prdSku.setSkuId(skuId);
        prdSku.setSkuName(updateDTO.getSkuName());
        prdSku.setSkuSpecs(updateDTO.getSkuSpecs());
        prdSku.setPrice(updateDTO.getPrice());
        skuMapper.updateById(prdSku);

        // prd_sku_attr
        skuAttrService.remove(new LambdaQueryWrapper<PrdSkuAttr>().eq(PrdSkuAttr::getSkuId, skuId));
        List<PrdSkuAttr> prdSkuAttrList = updateDTO.getSpecs().stream().map(item -> {
            PrdSkuAttr prdSkuAttr = new PrdSkuAttr();
            prdSkuAttr.setSkuId(skuId);
            prdSkuAttr.setAttrId(item.getAttrId());
            prdSkuAttr.setValueId(item.getValueId());
            return prdSkuAttr;
        }).toList();
        skuAttrService.saveBatch(prdSkuAttrList);

        // prd_stock
        PrdStock prdStock = new PrdStock();
        prdStock.setSkuId(skuId);
        prdStock.setQuantity(updateDTO.getQuantity());
        stockMapper.update(prdStock,
                new LambdaQueryWrapper<PrdStock>()
                        .eq(PrdStock::getSkuId, skuId)
                        .eq(PrdStock::getWarehouseCode, "DEFAULT"));

        // prd_spu
        Long spuId = updateDTO.getSpuId();
        List<Long> skuIds = skuMapper.selectList(new LambdaQueryWrapper<PrdSku>()
                .eq(PrdSku::getSpuId, spuId))
                .stream()
                .map(item -> item.getSkuId())
                .toList();
        int sum = stockMapper.selectList(new LambdaQueryWrapper<PrdStock>()
                        .in(PrdStock::getSkuId, skuIds)).stream()
                .mapToInt(PrdStock::getQuantity).sum();
        PrdSpu prdSkuStock = new PrdSpu();
        prdSkuStock.setSpuId(spuId);

        List<PrdSku> prdSkus = skuMapper.selectList(new LambdaQueryWrapper<PrdSku>()
                .in(PrdSku::getSpuId, spuId));
        BigDecimal min = prdSkus.stream()
                .map(PrdSku::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal max = prdSkus.stream()
                .map(PrdSku::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        prdSkuStock.setMinPrice(min);
        prdSkuStock.setMaxPrice(max);
        prdSkuStock.setTotalStock(sum);
        spuMapper.updateById(prdSkuStock);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSku(Long skuId) {

        PrdSku prdSku = skuMapper.selectById(skuId);

        // prd_sku
        skuMapper.deleteById(skuId);
        // prd_sku_attr
        skuAttrService.remove(new LambdaQueryWrapper<PrdSkuAttr>()
                .eq(PrdSkuAttr::getSkuId, skuId));

        // prd_stock
        stockMapper.delete(new LambdaQueryWrapper<PrdStock>()
                .eq(PrdStock::getSkuId, skuId)
                .eq(PrdStock::getWarehouseCode, "DEFAULT"));

        // prd_spu
        Long spuId = prdSku.getSpuId();
        List<Long> skuIds = skuMapper.selectList(new LambdaQueryWrapper<PrdSku>()
                .eq(PrdSku::getSpuId, spuId))
                .stream()
                .map(item -> item.getSkuId()).toList();
        int sum = stockMapper.selectList(new LambdaQueryWrapper<PrdStock>()
                        .in(PrdStock::getSkuId, skuIds)).stream()
                .mapToInt(PrdStock::getQuantity).sum();
        PrdSpu prdSkuStock = new PrdSpu();
        prdSkuStock.setSpuId(spuId);

        List<PrdSku> prdSkus = skuMapper.selectList(new LambdaQueryWrapper<PrdSku>()
                .in(PrdSku::getSpuId, spuId));
        BigDecimal min = prdSkus.stream()
                .map(PrdSku::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal max = prdSkus.stream()
                .map(PrdSku::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        prdSkuStock.setMinPrice(min);
        prdSkuStock.setMaxPrice(max);
        prdSkuStock.setTotalStock(sum);
        spuMapper.updateById(prdSkuStock);

    }

    private void addPrdSpuDetail(Long spuId, String description, String detailHtml) {
        PrdSpuDetail prdSpuDetail = new PrdSpuDetail();
        prdSpuDetail.setSpuId(spuId);
        prdSpuDetail.setDescription(description);
        prdSpuDetail.setDetailHtml(detailHtml);
        spuDetailMapper.insert(prdSpuDetail);
    }

    private void addPrdSpuImage(Long spuId, String coverUrl, List<String> images) {
        List<PrdSpuImage> prdSpuImageList = new ArrayList<>();
        if (StrUtil.isNotEmpty(coverUrl)) {
            PrdSpuImage cover = new PrdSpuImage();
            cover.setSpuId(spuId);
            cover.setImageUrl(coverUrl);
            cover.setMainFlag("1");
            cover.setOrderNum(1);
            prdSpuImageList.add(cover);
        }
        if (CollUtil.isNotEmpty(images)) {
            images.forEach(item -> {
                PrdSpuImage image = new PrdSpuImage();
                image.setSpuId(spuId);
                image.setImageUrl(item);
                image.setMainFlag("0");
                image.setOrderNum(prdSpuImageList.size() + 1);
                prdSpuImageList.add(image);
            });
        }
        if (CollUtil.isNotEmpty(prdSpuImageList)) {
            spuImageService.saveBatch(prdSpuImageList);
        }
    }
}
