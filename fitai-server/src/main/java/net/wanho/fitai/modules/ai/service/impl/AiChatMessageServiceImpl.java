package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiChatMessageMapper;
import net.wanho.fitai.modules.ai.model.dto.AiChatMessageAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatMessageQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatMessageUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiChatMessage;
import net.wanho.fitai.modules.ai.model.vo.AiChatMessageVO;
import net.wanho.fitai.modules.ai.service.AiChatMessageServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiChatMessageServiceImpl extends MPJBaseServiceImpl<AiChatMessageMapper, AiChatMessage> implements AiChatMessageServiceI {

    private final Converter converter;

    @Override
    public IPage<AiChatMessageVO> findByPage(AiChatMessageQueryDTO queryDto) {

        IPage<AiChatMessage> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getModelCode()), AiChatMessage::getModelCode, queryDto.getModelCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiChatMessage::getStatus, queryDto.getStatus());
        IPage<AiChatMessage> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiChatMessageVO.class));

    }

    @Override
    public void add(AiChatMessageAddDTO addDto) {
        AiChatMessage aichatmessage = converter.convert(addDto, AiChatMessage.class);
        this.save(aichatmessage);
    }

    @Override
    public void update(AiChatMessageUpdateDTO updateDto) {
        AiChatMessage aichatmessage = converter.convert(updateDto, AiChatMessage.class);
        this.updateById(aichatmessage);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiChatMessageVO findById(Long id) {
        AiChatMessage entity = this.getById(id);
        return converter.convert(entity, AiChatMessageVO.class);
    }
}
