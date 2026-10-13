package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdLocalMessageMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdLocalMessageAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdLocalMessageQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdLocalMessageUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdLocalMessage;
import net.wanho.fitai.modules.mall.model.vo.PrdLocalMessageVO;
import net.wanho.fitai.modules.mall.service.PrdLocalMessageServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdLocalMessageServiceImpl extends MPJBaseServiceImpl<PrdLocalMessageMapper, PrdLocalMessage> implements PrdLocalMessageServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdLocalMessageVO> findByPage(PrdLocalMessageQueryDTO queryDto) {

        IPage<PrdLocalMessage> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdLocalMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getBizKey()), PrdLocalMessage::getBizKey, queryDto.getBizKey());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getExchangeName()), PrdLocalMessage::getExchangeName, queryDto.getExchangeName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRoutingKey()), PrdLocalMessage::getRoutingKey, queryDto.getRoutingKey());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), PrdLocalMessage::getStatus, queryDto.getStatus());
        IPage<PrdLocalMessage> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdLocalMessageVO.class));

    }

    @Override
    public void add(PrdLocalMessageAddDTO addDto) {
        PrdLocalMessage prdlocalmessage = converter.convert(addDto, PrdLocalMessage.class);
        this.save(prdlocalmessage);
    }

    @Override
    public void update(PrdLocalMessageUpdateDTO updateDto) {
        PrdLocalMessage prdlocalmessage = converter.convert(updateDto, PrdLocalMessage.class);
        this.updateById(prdlocalmessage);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdLocalMessageVO findById(String id) {
        PrdLocalMessage entity = this.getById(id);
        return converter.convert(entity, PrdLocalMessageVO.class);
    }
}
