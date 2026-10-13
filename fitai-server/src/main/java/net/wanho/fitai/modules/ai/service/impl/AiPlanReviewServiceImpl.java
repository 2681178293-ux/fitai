package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiPlanReviewMapper;
import net.wanho.fitai.modules.ai.model.dto.AiPlanReviewAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPlanReviewQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPlanReviewUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiPlanReview;
import net.wanho.fitai.modules.ai.model.vo.AiPlanReviewVO;
import net.wanho.fitai.modules.ai.service.AiPlanReviewServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiPlanReviewServiceImpl extends MPJBaseServiceImpl<AiPlanReviewMapper, AiPlanReview> implements AiPlanReviewServiceI {

    private final Converter converter;

    @Override
    public IPage<AiPlanReviewVO> findByPage(AiPlanReviewQueryDTO queryDto) {

        IPage<AiPlanReview> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiPlanReview> wrapper = new LambdaQueryWrapper<>();
        IPage<AiPlanReview> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiPlanReviewVO.class));

    }

    @Override
    public void add(AiPlanReviewAddDTO addDto) {
        AiPlanReview aiplanreview = converter.convert(addDto, AiPlanReview.class);
        this.save(aiplanreview);
    }

    @Override
    public void update(AiPlanReviewUpdateDTO updateDto) {
        AiPlanReview aiplanreview = converter.convert(updateDto, AiPlanReview.class);
        this.updateById(aiplanreview);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiPlanReviewVO findById(Long id) {
        AiPlanReview entity = this.getById(id);
        return converter.convert(entity, AiPlanReviewVO.class);
    }
}
