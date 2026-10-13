package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.OrdPaymentChannelMapper;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentChannelAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentChannelQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentChannelUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdPaymentChannel;
import net.wanho.fitai.modules.mall.model.vo.OrdPaymentChannelVO;
import net.wanho.fitai.modules.mall.service.OrdPaymentChannelServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrdPaymentChannelServiceImpl extends MPJBaseServiceImpl<OrdPaymentChannelMapper, OrdPaymentChannel> implements OrdPaymentChannelServiceI {

    private final Converter converter;

    @Override
    public IPage<OrdPaymentChannelVO> findByPage(OrdPaymentChannelQueryDTO queryDto) {

        IPage<OrdPaymentChannel> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<OrdPaymentChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getChannelName()), OrdPaymentChannel::getChannelName, queryDto.getChannelName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getChannelCode()), OrdPaymentChannel::getChannelCode, queryDto.getChannelCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), OrdPaymentChannel::getStatus, queryDto.getStatus());
        IPage<OrdPaymentChannel> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, OrdPaymentChannelVO.class));

    }

    @Override
    public void add(OrdPaymentChannelAddDTO addDto) {
        OrdPaymentChannel ordpaymentchannel = converter.convert(addDto, OrdPaymentChannel.class);
        this.save(ordpaymentchannel);
    }

    @Override
    public void update(OrdPaymentChannelUpdateDTO updateDto) {
        OrdPaymentChannel ordpaymentchannel = converter.convert(updateDto, OrdPaymentChannel.class);
        this.updateById(ordpaymentchannel);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public OrdPaymentChannelVO findById(Long id) {
        OrdPaymentChannel entity = this.getById(id);
        return converter.convert(entity, OrdPaymentChannelVO.class);
    }
}
