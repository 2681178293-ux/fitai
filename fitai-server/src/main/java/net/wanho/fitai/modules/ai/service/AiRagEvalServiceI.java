package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiRagEvalAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiRagEvalQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiRagEvalUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiRagEval;
import net.wanho.fitai.modules.ai.model.vo.AiRagEvalVO;

public interface AiRagEvalServiceI extends MPJBaseService<AiRagEval> {
    IPage<AiRagEvalVO> findByPage(AiRagEvalQueryDTO queryDto);

    void add(AiRagEvalAddDTO addDto);

    void update(AiRagEvalUpdateDTO updateDto);

    void delete(String id);

    AiRagEvalVO findById(Long id);

}
