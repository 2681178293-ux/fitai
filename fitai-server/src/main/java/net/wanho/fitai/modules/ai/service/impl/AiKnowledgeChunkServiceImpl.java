package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiKnowledgeChunkMapper;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeChunkAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeChunkQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeChunkUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiKnowledgeChunk;
import net.wanho.fitai.modules.ai.model.vo.AiKnowledgeChunkVO;
import net.wanho.fitai.modules.ai.service.AiKnowledgeChunkServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiKnowledgeChunkServiceImpl extends MPJBaseServiceImpl<AiKnowledgeChunkMapper, AiKnowledgeChunk> implements AiKnowledgeChunkServiceI {

    private final Converter converter;

    @Override
    public IPage<AiKnowledgeChunkVO> findByPage(AiKnowledgeChunkQueryDTO queryDto) {

        IPage<AiKnowledgeChunk> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiKnowledgeChunk> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRedisDocKey()), AiKnowledgeChunk::getRedisDocKey, queryDto.getRedisDocKey());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRedisIndexName()), AiKnowledgeChunk::getRedisIndexName, queryDto.getRedisIndexName());
        IPage<AiKnowledgeChunk> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiKnowledgeChunkVO.class));

    }

    @Override
    public void add(AiKnowledgeChunkAddDTO addDto) {
        AiKnowledgeChunk aiknowledgechunk = converter.convert(addDto, AiKnowledgeChunk.class);
        this.save(aiknowledgechunk);
    }

    @Override
    public void update(AiKnowledgeChunkUpdateDTO updateDto) {
        AiKnowledgeChunk aiknowledgechunk = converter.convert(updateDto, AiKnowledgeChunk.class);
        this.updateById(aiknowledgechunk);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiKnowledgeChunkVO findById(Long id) {
        AiKnowledgeChunk entity = this.getById(id);
        return converter.convert(entity, AiKnowledgeChunkVO.class);
    }
}
