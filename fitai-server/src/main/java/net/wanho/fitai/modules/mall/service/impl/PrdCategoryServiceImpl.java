package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.exception.CustomException;
import net.wanho.fitai.modules.mall.code.MallCode;
import net.wanho.fitai.modules.mall.mapper.PrdCategoryMapper;
import net.wanho.fitai.modules.mall.mapper.PrdSkuAttrMapper;
import net.wanho.fitai.modules.mall.mapper.PrdSkuMapper;
import net.wanho.fitai.modules.mall.mapper.PrdSpuMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.*;
import net.wanho.fitai.modules.mall.model.vo.PrdCategoryVO;
import net.wanho.fitai.modules.mall.service.PrdCategoryAttrServiceI;
import net.wanho.fitai.modules.mall.service.PrdCategoryServiceI;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrdCategoryServiceImpl extends MPJBaseServiceImpl<PrdCategoryMapper, PrdCategory> implements PrdCategoryServiceI {

    private final Converter converter;
    private final PrdCategoryAttrServiceI prdCategoryAttrService;
    private final PrdSkuAttrMapper prdSkuAttrMapper;
    private final PrdSpuMapper  prdSpuMapper;
    private final PrdSkuMapper prdSkuMapper;


    @Override
    public IPage<PrdCategoryVO> findByPage(PrdCategoryQueryDTO queryDto) {

        IPage<PrdCategory> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCategoryName()), PrdCategory::getCategoryName, queryDto.getCategoryName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), PrdCategory::getStatus, queryDto.getStatus());
        IPage<PrdCategory> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdCategoryVO.class));

    }

    @Override
    public void add(PrdCategoryAddDTO addDto) {
        PrdCategory prdcategory = converter.convert(addDto, PrdCategory.class);
        this.save(prdcategory);
    }


    /**
     * 更新分类：先改基础信息；再校验待移除属性是否被 SKU 引用（有则失败）；
     * 最后按 attrIds 全量替换关联（先删旧再插新）。
     * prd_attr   prd_attr_value
     */
    @Override
    public void update(PrdCategoryUpdateDTO updateDto) {
        PrdCategory prdcategory = converter.convert(updateDto, PrdCategory.class);
        //将分类信息更新起来，不包括关联属性
        this.updateById(prdcategory);

        //先判断是否有关联的sku
        judgeDelete(updateDto, prdcategory);

        List<Long> attrIds = updateDto.getAttrIds();
        if (CollUtil.isNotEmpty(attrIds)) {
            //先删除旧的关联属性
            prdCategoryAttrService.remove(new LambdaQueryWrapper<PrdCategoryAttr>()
                    .eq(PrdCategoryAttr::getCategoryId, updateDto.getCategoryId()));

            //将新增的关联属性保存起来
            List<PrdCategoryAttr> categoryAttrList = attrIds.stream().map(item -> {
                PrdCategoryAttr prdCategoryAttr = new PrdCategoryAttr();
                prdCategoryAttr.setCategoryId(updateDto.getCategoryId());
                prdCategoryAttr.setAttrId(item);
                return prdCategoryAttr;
            }).toList();

            // 批量保存关联属性
            if (CollUtil.isNotEmpty(categoryAttrList))
                prdCategoryAttrService.saveBatch(categoryAttrList);
        }


    }

    private void judgeDelete(PrdCategoryUpdateDTO updateDto, PrdCategory prdcategory) {
        //先过滤出spuId商品集合
        List<Long> spuIds = prdSpuMapper.selectList(new LambdaQueryWrapper<PrdSpu>()
                        .eq(PrdSpu::getCategoryId, updateDto.getCategoryId()))
                .stream().map(PrdSpu::getSpuId).toList();

        if(CollUtil.isEmpty(spuIds)) return;

        //再过滤出skuId规格集合
        List<Long> skuIds = prdSkuMapper.selectList(new LambdaQueryWrapper<PrdSku>()
                        .in(PrdSku::getSpuId, spuIds))
                .stream().map(PrdSku::getSkuId).toList();

        if(CollUtil.isEmpty(skuIds)) return;

        // 判断 是否关联sku   算出被取消勾选则属性id集合
        //attrIds1是当前分类的属性id集合  attrIds2是前端传入的属性id集合
        List<Long> attrIds1 = prdCategoryAttrService.list(
                new LambdaQueryWrapper<PrdCategoryAttr>()
                        .eq(PrdCategoryAttr::getCategoryId, prdcategory.getCategoryId()
                        )).stream().map(PrdCategoryAttr::getAttrId).toList();
        List<Long> attrIds2 = updateDto.getAttrIds();
        List<Long> diff = new ArrayList<>(attrIds1);
        diff.removeAll(attrIds2);

        if (CollUtil.isNotEmpty(diff)) {
            List<PrdSkuAttr> prdSkuAttrs = prdSkuAttrMapper.selectList(
                    new LambdaQueryWrapper<PrdSkuAttr>()
                            .in(PrdSkuAttr::getAttrId, diff)
                            .in(PrdSkuAttr::getSkuId,skuIds)
            );
            if (CollUtil.isNotEmpty(prdSkuAttrs)) {
                throw new CustomException(MallCode.SKU_EXISTS_REFER);
            }
        }
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdCategoryVO findById(Long id) {
        return this.baseMapper.selectJoinOne(
                PrdCategoryVO.class,
                new MPJLambdaWrapper<PrdCategory>()
                        .selectAll(PrdCategory.class)
                        .selectCollection(PrdCategoryAttr.class, PrdCategoryVO::getAttrIds, map -> map
                                .id(PrdCategoryAttr::getAttrId)
                                .result(PrdCategoryAttr::getAttrId))
                        .leftJoin(PrdCategoryAttr.class, on -> on
                                .eq(PrdCategoryAttr::getCategoryId, PrdCategory::getCategoryId))
                        .leftJoin(PrdAttr.class, on -> on
                                .eq(PrdAttr::getAttrId, PrdCategoryAttr::getAttrId))
                        .eq(PrdCategory::getCategoryId, id)
                        .orderByAsc(PrdAttr::getOrderNum));
    }

    @Override
    public List<PrdCategoryVO> tree(PrdCategoryQueryDTO queryDto) {

        //LambdaQueryWrapper<PrdCategory> wrapper = new LambdaQueryWrapper<>();
        //wrapper.orderByAsc(PrdCategory::getParentId);
        //wrapper.orderByAsc(PrdCategory::getOrderNum);
        //wrapper.orderByAsc(PrdCategory::getCategoryId);
        //List<PrdCategoryVO> all = this.list(wrapper).stream().map(item -> converter.convert(item, PrdCategoryVO.class)).collect(Collectors.toList());

        List<PrdCategoryVO> all = this.baseMapper.selectJoinList(
                PrdCategoryVO.class,
                new MPJLambdaWrapper<PrdCategory>()
                        .selectAll(PrdCategory.class)
                        .selectCollection(PrdAttr.class, PrdCategoryVO::getAttrNames, map -> map
                                .result(PrdAttr::getAttrName))
                        .leftJoin(PrdCategoryAttr.class, on -> on
                                .eq(PrdCategoryAttr::getCategoryId, PrdCategory::getCategoryId))
                        .leftJoin(PrdAttr.class, on -> on
                                .eq(PrdAttr::getAttrId, PrdCategoryAttr::getAttrId))
                        .orderByAsc(PrdAttr::getOrderNum));

        return recursion(0L, all);

    }

    @Override
    public void updateStatus(Long categoryId, String status) {
        PrdCategory prdCategory = new PrdCategory();
        prdCategory.setCategoryId(categoryId);
        prdCategory.setStatus(status);
        this.updateById(prdCategory);
    }

    private List<PrdCategoryVO> recursion(long parentId, List<PrdCategoryVO> all) {
        return all.stream()
                .filter(item -> item.getParentId().equals(parentId))
                .peek(item -> item.setChildren(recursion(item.getCategoryId(), all)))
                .toList();
    }
}
