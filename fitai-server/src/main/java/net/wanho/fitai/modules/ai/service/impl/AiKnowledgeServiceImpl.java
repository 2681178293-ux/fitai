package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiKnowledgeMapper;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiKnowledge;
import net.wanho.fitai.modules.ai.model.vo.AiKnowledgeVO;
import net.wanho.fitai.modules.ai.service.AiKnowledgeServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiKnowledgeServiceImpl extends MPJBaseServiceImpl<AiKnowledgeMapper, AiKnowledge> implements AiKnowledgeServiceI {

    private final Converter converter;

    @Override
    public IPage<AiKnowledgeVO> findByPage(AiKnowledgeQueryDTO queryDto) {

        IPage<AiKnowledge> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiKnowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getKbName()), AiKnowledge::getKbName, queryDto.getKbName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getKbCode()), AiKnowledge::getKbCode, queryDto.getKbCode());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRedisIndexName()), AiKnowledge::getRedisIndexName, queryDto.getRedisIndexName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiKnowledge::getStatus, queryDto.getStatus());
        IPage<AiKnowledge> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiKnowledgeVO.class));

    }

    @Override
    public void add(AiKnowledgeAddDTO addDto) {
        AiKnowledge aiknowledge = converter.convert(addDto, AiKnowledge.class);
        this.save(aiknowledge);
    }

    @Override
    public void update(AiKnowledgeUpdateDTO updateDto) {
        AiKnowledge aiknowledge = converter.convert(updateDto, AiKnowledge.class);
        this.updateById(aiknowledge);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiKnowledgeVO findById(Long id) {
        AiKnowledge entity = this.getById(id);
        return converter.convert(entity, AiKnowledgeVO.class);
    }
}
