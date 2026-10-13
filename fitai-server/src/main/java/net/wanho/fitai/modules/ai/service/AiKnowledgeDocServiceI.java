package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeDocAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeDocQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeDocUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiKnowledgeDoc;
import net.wanho.fitai.modules.ai.model.vo.AiKnowledgeDocVO;

public interface AiKnowledgeDocServiceI extends MPJBaseService<AiKnowledgeDoc> {
    IPage<AiKnowledgeDocVO> findByPage(AiKnowledgeDocQueryDTO queryDto);

    void add(AiKnowledgeDocAddDTO addDto);

    void update(AiKnowledgeDocUpdateDTO updateDto);

    void delete(String id);

    AiKnowledgeDocVO findById(Long id);

}
