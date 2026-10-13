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
import net.wanho.fitai.modules.mall.mapper.PrdAttrMapper;
import net.wanho.fitai.modules.mall.mapper.PrdAttrValueMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdAttr;
import net.wanho.fitai.modules.mall.model.po.PrdAttrValue;
import net.wanho.fitai.modules.mall.model.vo.PrdAttrVO;
import net.wanho.fitai.modules.mall.model.vo.PrdAttrValueVO;
import net.wanho.fitai.modules.mall.service.PrdAttrServiceI;
import net.wanho.fitai.modules.mall.service.PrdAttrValueServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrdAttrServiceImpl extends MPJBaseServiceImpl<PrdAttrMapper, PrdAttr> implements PrdAttrServiceI {

    private final Converter converter;
    private final PrdAttrValueMapper prdAttrValueMapper;
    private final PrdAttrValueServiceI prdAttrValueService;

    @Override
    public IPage<PrdAttrVO> findByPage(PrdAttrQueryDTO queryDto) {

        IPage<PrdAttr> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdAttr> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getAttrName()), PrdAttr::getAttrName, queryDto.getAttrName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getAttrType()), PrdAttr::getAttrType, queryDto.getAttrType());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), PrdAttr::getStatus, queryDto.getStatus());
        IPage<PrdAttr> result = this.page(page, wrapper);

        //拿到所有attrId
        List<Long> attrIds = result.getRecords().stream().map(PrdAttr::getAttrId).toList();
        //根据attrId查询所有属性值,并按orderNum排序
        List<PrdAttrValue> prdAttrValues = prdAttrValueMapper
                .selectList(new LambdaQueryWrapper<PrdAttrValue>()
                        .in(PrdAttrValue::getAttrId, attrIds)
                        .orderByAsc(PrdAttrValue::getOrderNum));
        //按attrId分组,并收集到Map中   Map是attrId和所有属性值列表的映射关系
        Map<Long,List<PrdAttrValue>> collect = prdAttrValues.stream().collect(Collectors.groupingBy(PrdAttrValue::getAttrId, Collectors.toList()));
        //把Map<Long,List<PrdAttrValue>> 转换为 Map<Long,List<PrdAttrValueVO>>
        Map<Long,List<PrdAttrValueVO>> prdAttrValueVOMap =collect.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,entry -> entry.getValue().stream()
                        .map(prdAttrValue -> converter.convert(prdAttrValue, PrdAttrValueVO.class))
                        .collect(Collectors.toList())));

        return result.convert(item -> {
            PrdAttrVO prdAttrVO = converter.convert(item, PrdAttrVO.class);
            prdAttrVO.setValues(prdAttrValueVOMap.get(item.getAttrId()));
            return prdAttrVO;
        });

    }

    @Override
    public void add(PrdAttrAddDTO addDto) {
        PrdAttr prdattr = converter.convert(addDto, PrdAttr.class);
        this.save(prdattr);

        // save attrValue
        if(CollUtil.isNotEmpty(addDto.getValues())) {

            List<PrdAttrValue> prdAttrValueList = addDto.getValues().stream().map(item -> {
                PrdAttrValue prdAttrValue = converter.convert(item, PrdAttrValue.class);
                prdAttrValue.setAttrId(prdattr.getAttrId()); // 设置属性ID
                prdAttrValue.setAttrValue(item.getAttrValue()); // 设置属性值
                prdAttrValue.setOrderNum(item.getOrderNum()); // 设置排序
                prdAttrValue.setStatus(item.getStatus()); // 设置状态
                return prdAttrValue;
            }).toList();

            if(CollUtil.isNotEmpty(prdAttrValueList))
                prdAttrValueService.saveBatch(prdAttrValueList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PrdAttrUpdateDTO updateDto) {
        PrdAttr prdattr = converter.convert(updateDto, PrdAttr.class);
        this.updateById(prdattr);

        // remove - attrValue
        prdAttrValueMapper.delete(new LambdaQueryWrapper<PrdAttrValue>()
                .eq(PrdAttrValue::getAttrId, updateDto.getAttrId())
        );
        // add - attrValue
        if(CollUtil.isNotEmpty(updateDto.getValues())) {
            List<PrdAttrValue> prdAttrValueList = updateDto.getValues().stream().map(item -> {
                PrdAttrValue prdAttrValue = new PrdAttrValue();
                prdAttrValue.setAttrId(updateDto.getAttrId()); // 设置属性ID
                prdAttrValue.setAttrValue(item.getAttrValue()); // 设置属性值
                prdAttrValue.setOrderNum(item.getOrderNum()); // 设置排序
                prdAttrValue.setStatus(item.getStatus()); // 设置状态
                return prdAttrValue;
            }).toList();

            if(CollUtil.isNotEmpty(prdAttrValueList)){
                prdAttrValueService.saveBatch(prdAttrValueList);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());

        // remove - attrValue
        prdAttrValueMapper.delete(new LambdaQueryWrapper<PrdAttrValue>()
                .eq(PrdAttrValue::getAttrId, id)
        );
    }

    @Override
    public PrdAttrVO findById(Long id) {
        MPJLambdaWrapper<PrdAttr> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(PrdAttr.class);
        //     attrValue、orderNum、status ; 填充到PrdAttr对象的values集合中
        wrapper.selectCollection(PrdAttrValue.class, PrdAttrVO::getValues);
        wrapper.leftJoin(PrdAttrValue.class, PrdAttrValue::getAttrId, PrdAttr::getAttrId);
        wrapper.eq(PrdAttr::getAttrId, id); // 设置条件
        wrapper.orderByAsc(PrdAttr::getOrderNum); // 按照排序字段进行排序
        wrapper.orderByAsc(PrdAttrValue::getOrderNum); // 按照排序字段进行排序

        return this.selectJoinOne(PrdAttrVO.class, wrapper);
    }
}
