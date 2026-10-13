package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiChatSessionMapper;
import net.wanho.fitai.modules.ai.model.dto.AiChatSessionAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatSessionQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatSessionUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiChatSession;
import net.wanho.fitai.modules.ai.model.vo.AiChatSessionVO;
import net.wanho.fitai.modules.ai.service.AiChatSessionServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiChatSessionServiceImpl extends MPJBaseServiceImpl<AiChatSessionMapper, AiChatSession> implements AiChatSessionServiceI {

    private final Converter converter;

    @Override
    public IPage<AiChatSessionVO> findByPage(AiChatSessionQueryDTO queryDto) {

        IPage<AiChatSession> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getModelCode()), AiChatSession::getModelCode, queryDto.getModelCode());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSessionTitle()), AiChatSession::getSessionTitle, queryDto.getSessionTitle());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiChatSession::getStatus, queryDto.getStatus());
        IPage<AiChatSession> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiChatSessionVO.class));

    }

    @Override
    public void add(AiChatSessionAddDTO addDto) {
        AiChatSession aichatsession = converter.convert(addDto, AiChatSession.class);
        this.save(aichatsession);
    }

    @Override
    public void update(AiChatSessionUpdateDTO updateDto) {
        AiChatSession aichatsession = converter.convert(updateDto, AiChatSession.class);
        this.updateById(aichatsession);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiChatSessionVO findById(Long id) {
        AiChatSession entity = this.getById(id);
        return converter.convert(entity, AiChatSessionVO.class);
    }
}
