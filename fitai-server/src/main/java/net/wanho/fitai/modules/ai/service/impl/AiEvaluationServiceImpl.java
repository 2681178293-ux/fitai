package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiEvaluationMapper;
import net.wanho.fitai.modules.ai.model.dto.AiEvaluationAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiEvaluationQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiEvaluationUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiEvaluation;
import net.wanho.fitai.modules.ai.model.vo.AiEvaluationVO;
import net.wanho.fitai.modules.ai.service.AiEvaluationServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiEvaluationServiceImpl extends MPJBaseServiceImpl<AiEvaluationMapper, AiEvaluation> implements AiEvaluationServiceI {

    private final Converter converter;

    @Override
    public IPage<AiEvaluationVO> findByPage(AiEvaluationQueryDTO queryDto) {

        IPage<AiEvaluation> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getEvalName()), AiEvaluation::getEvalName, queryDto.getEvalName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiEvaluation::getStatus, queryDto.getStatus());
        IPage<AiEvaluation> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiEvaluationVO.class));

    }

    @Override
    public void add(AiEvaluationAddDTO addDto) {
        AiEvaluation aievaluation = converter.convert(addDto, AiEvaluation.class);
        this.save(aievaluation);
    }

    @Override
    public void update(AiEvaluationUpdateDTO updateDto) {
        AiEvaluation aievaluation = converter.convert(updateDto, AiEvaluation.class);
        this.updateById(aievaluation);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiEvaluationVO findById(Long id) {
        AiEvaluation entity = this.getById(id);
        return converter.convert(entity, AiEvaluationVO.class);
    }
}
