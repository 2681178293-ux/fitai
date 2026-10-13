package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiEvaluationAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiEvaluationQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiEvaluationUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiEvaluation;
import net.wanho.fitai.modules.ai.model.vo.AiEvaluationVO;

public interface AiEvaluationServiceI extends MPJBaseService<AiEvaluation> {
    IPage<AiEvaluationVO> findByPage(AiEvaluationQueryDTO queryDto);

    void add(AiEvaluationAddDTO addDto);

    void update(AiEvaluationUpdateDTO updateDto);

    void delete(String id);

    AiEvaluationVO findById(Long id);

}
