package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.OrdLocalMessageMapper;
import net.wanho.fitai.modules.mall.model.dto.OrdLocalMessageAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLocalMessageQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLocalMessageUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdLocalMessage;
import net.wanho.fitai.modules.mall.model.vo.OrdLocalMessageVO;
import net.wanho.fitai.modules.mall.service.OrdLocalMessageServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrdLocalMessageServiceImpl extends MPJBaseServiceImpl<OrdLocalMessageMapper, OrdLocalMessage> implements OrdLocalMessageServiceI {

    private final Converter converter;

    @Override
    public IPage<OrdLocalMessageVO> findByPage(OrdLocalMessageQueryDTO queryDto) {

        IPage<OrdLocalMessage> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<OrdLocalMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getBizKey()), OrdLocalMessage::getBizKey, queryDto.getBizKey());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getExchangeName()), OrdLocalMessage::getExchangeName, queryDto.getExchangeName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRoutingKey()), OrdLocalMessage::getRoutingKey, queryDto.getRoutingKey());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), OrdLocalMessage::getStatus, queryDto.getStatus());
        IPage<OrdLocalMessage> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, OrdLocalMessageVO.class));

    }

    @Override
    public void add(OrdLocalMessageAddDTO addDto) {
        OrdLocalMessage ordlocalmessage = converter.convert(addDto, OrdLocalMessage.class);
        this.save(ordlocalmessage);
    }

    @Override
    public void update(OrdLocalMessageUpdateDTO updateDto) {
        OrdLocalMessage ordlocalmessage = converter.convert(updateDto, OrdLocalMessage.class);
        this.updateById(ordlocalmessage);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public OrdLocalMessageVO findById(String id) {
        OrdLocalMessage entity = this.getById(id);
        return converter.convert(entity, OrdLocalMessageVO.class);
    }
}
