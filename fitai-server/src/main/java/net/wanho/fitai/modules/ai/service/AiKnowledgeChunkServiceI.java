package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeChunkAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeChunkQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeChunkUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiKnowledgeChunk;
import net.wanho.fitai.modules.ai.model.vo.AiKnowledgeChunkVO;

public interface AiKnowledgeChunkServiceI extends MPJBaseService<AiKnowledgeChunk> {
    IPage<AiKnowledgeChunkVO> findByPage(AiKnowledgeChunkQueryDTO queryDto);

    void add(AiKnowledgeChunkAddDTO addDto);

    void update(AiKnowledgeChunkUpdateDTO updateDto);

    void delete(String id);

    AiKnowledgeChunkVO findById(Long id);

}
