package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.OrdOrderMapper;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdOrder;
import net.wanho.fitai.modules.mall.model.vo.OrdOrderVO;
import net.wanho.fitai.modules.mall.service.OrdOrderServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrdOrderServiceImpl extends MPJBaseServiceImpl<OrdOrderMapper, OrdOrder> implements OrdOrderServiceI {

    private final Converter converter;

    @Override
    public IPage<OrdOrderVO> findByPage(OrdOrderQueryDTO queryDto) {

        IPage<OrdOrder> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<OrdOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOrderNo()), OrdOrder::getOrderNo, queryDto.getOrderNo());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCustomerName()), OrdOrder::getCustomerName, queryDto.getCustomerName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getReceiverName()), OrdOrder::getReceiverName, queryDto.getReceiverName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getReceiverPhone()), OrdOrder::getReceiverPhone, queryDto.getReceiverPhone());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), OrdOrder::getStatus, queryDto.getStatus());
        IPage<OrdOrder> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, OrdOrderVO.class));

    }

    @Override
    public void add(OrdOrderAddDTO addDto) {
        OrdOrder ordorder = converter.convert(addDto, OrdOrder.class);
        this.save(ordorder);
    }

    @Override
    public void update(OrdOrderUpdateDTO updateDto) {
        OrdOrder ordorder = converter.convert(updateDto, OrdOrder.class);
        this.updateById(ordorder);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public OrdOrderVO findById(Long id) {
        OrdOrder entity = this.getById(id);
        return converter.convert(entity, OrdOrderVO.class);
    }
}
