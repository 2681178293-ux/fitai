package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.SeckillOrderMapper;
import net.wanho.fitai.modules.marketing.model.dto.SeckillOrderAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillOrderQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillOrderUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.SeckillOrder;
import net.wanho.fitai.modules.marketing.model.vo.SeckillOrderVO;
import net.wanho.fitai.modules.marketing.service.SeckillOrderServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SeckillOrderServiceImpl extends MPJBaseServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderServiceI {

    private final Converter converter;

    @Override
    public IPage<SeckillOrderVO> findByPage(SeckillOrderQueryDTO queryDto) {

        IPage<SeckillOrder> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SeckillOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOrderNo()), SeckillOrder::getOrderNo, queryDto.getOrderNo());
        IPage<SeckillOrder> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SeckillOrderVO.class));

    }

    @Override
    public void add(SeckillOrderAddDTO addDto) {
        SeckillOrder seckillorder = converter.convert(addDto, SeckillOrder.class);
        this.save(seckillorder);
    }

    @Override
    public void update(SeckillOrderUpdateDTO updateDto) {
        SeckillOrder seckillorder = converter.convert(updateDto, SeckillOrder.class);
        this.updateById(seckillorder);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SeckillOrderVO findById(Long id) {
        SeckillOrder entity = this.getById(id);
        return converter.convert(entity, SeckillOrderVO.class);
    }
}
