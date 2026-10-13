package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.OrdOrderItemMapper;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderItemAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderItemQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderItemUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdOrderItem;
import net.wanho.fitai.modules.mall.model.vo.OrdOrderItemVO;
import net.wanho.fitai.modules.mall.service.OrdOrderItemServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrdOrderItemServiceImpl extends MPJBaseServiceImpl<OrdOrderItemMapper, OrdOrderItem> implements OrdOrderItemServiceI {

    private final Converter converter;

    @Override
    public IPage<OrdOrderItemVO> findByPage(OrdOrderItemQueryDTO queryDto) {

        IPage<OrdOrderItem> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<OrdOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOrderNo()), OrdOrderItem::getOrderNo, queryDto.getOrderNo());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSkuName()), OrdOrderItem::getSkuName, queryDto.getSkuName());
        IPage<OrdOrderItem> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, OrdOrderItemVO.class));

    }

    @Override
    public void add(OrdOrderItemAddDTO addDto) {
        OrdOrderItem ordorderitem = converter.convert(addDto, OrdOrderItem.class);
        this.save(ordorderitem);
    }

    @Override
    public void update(OrdOrderItemUpdateDTO updateDto) {
        OrdOrderItem ordorderitem = converter.convert(updateDto, OrdOrderItem.class);
        this.updateById(ordorderitem);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public OrdOrderItemVO findById(Long id) {
        OrdOrderItem entity = this.getById(id);
        return converter.convert(entity, OrdOrderItemVO.class);
    }
}
