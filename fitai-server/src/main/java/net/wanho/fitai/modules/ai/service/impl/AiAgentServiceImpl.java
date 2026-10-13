package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiAgentMapper;
import net.wanho.fitai.modules.ai.model.dto.AiAgentAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiAgentQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiAgentUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiAgent;
import net.wanho.fitai.modules.ai.model.vo.AiAgentVO;
import net.wanho.fitai.modules.ai.service.AiAgentServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiAgentServiceImpl extends MPJBaseServiceImpl<AiAgentMapper, AiAgent> implements AiAgentServiceI {

    private final Converter converter;

    @Override
    public IPage<AiAgentVO> findByPage(AiAgentQueryDTO queryDto) {

        IPage<AiAgent> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiAgent> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getAgentName()), AiAgent::getAgentName, queryDto.getAgentName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getAgentCode()), AiAgent::getAgentCode, queryDto.getAgentCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiAgent::getStatus, queryDto.getStatus());
        IPage<AiAgent> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiAgentVO.class));

    }

    @Override
    public void add(AiAgentAddDTO addDto) {
        AiAgent aiagent = converter.convert(addDto, AiAgent.class);
        this.save(aiagent);
    }

    @Override
    public void update(AiAgentUpdateDTO updateDto) {
        AiAgent aiagent = converter.convert(updateDto, AiAgent.class);
        this.updateById(aiagent);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiAgentVO findById(Long id) {
        AiAgent entity = this.getById(id);
        return converter.convert(entity, AiAgentVO.class);
    }
}
