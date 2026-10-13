package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiKnowledge;
import net.wanho.fitai.modules.ai.model.vo.AiKnowledgeVO;

public interface AiKnowledgeServiceI extends MPJBaseService<AiKnowledge> {
    IPage<AiKnowledgeVO> findByPage(AiKnowledgeQueryDTO queryDto);

    void add(AiKnowledgeAddDTO addDto);

    void update(AiKnowledgeUpdateDTO updateDto);

    void delete(String id);

    AiKnowledgeVO findById(Long id);

}
