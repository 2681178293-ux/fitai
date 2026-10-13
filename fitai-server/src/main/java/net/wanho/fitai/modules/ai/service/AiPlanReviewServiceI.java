package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiPlanReviewAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPlanReviewQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPlanReviewUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiPlanReview;
import net.wanho.fitai.modules.ai.model.vo.AiPlanReviewVO;

public interface AiPlanReviewServiceI extends MPJBaseService<AiPlanReview> {
    IPage<AiPlanReviewVO> findByPage(AiPlanReviewQueryDTO queryDto);

    void add(AiPlanReviewAddDTO addDto);

    void update(AiPlanReviewUpdateDTO updateDto);

    void delete(String id);

    AiPlanReviewVO findById(Long id);

}
