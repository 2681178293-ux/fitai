package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiTraceMapper;
import net.wanho.fitai.modules.ai.model.dto.AiTraceAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTraceQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTraceUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiTrace;
import net.wanho.fitai.modules.ai.model.vo.AiTraceVO;
import net.wanho.fitai.modules.ai.service.AiTraceServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiTraceServiceImpl extends MPJBaseServiceImpl<AiTraceMapper, AiTrace> implements AiTraceServiceI {

    private final Converter converter;

    @Override
    public IPage<AiTraceVO> findByPage(AiTraceQueryDTO queryDto) {

        IPage<AiTrace> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiTrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getModelCode()), AiTrace::getModelCode, queryDto.getModelCode());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getAgentCode()), AiTrace::getAgentCode, queryDto.getAgentCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiTrace::getStatus, queryDto.getStatus());
        IPage<AiTrace> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiTraceVO.class));

    }

    @Override
    public void add(AiTraceAddDTO addDto) {
        AiTrace aitrace = converter.convert(addDto, AiTrace.class);
        this.save(aitrace);
    }

    @Override
    public void update(AiTraceUpdateDTO updateDto) {
        AiTrace aitrace = converter.convert(updateDto, AiTrace.class);
        this.updateById(aitrace);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiTraceVO findById(Long id) {
        AiTrace entity = this.getById(id);
        return converter.convert(entity, AiTraceVO.class);
    }
}
