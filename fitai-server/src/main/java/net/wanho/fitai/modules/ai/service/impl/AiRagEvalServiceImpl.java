package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiRagEvalMapper;
import net.wanho.fitai.modules.ai.model.dto.AiRagEvalAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiRagEvalQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiRagEvalUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiRagEval;
import net.wanho.fitai.modules.ai.model.vo.AiRagEvalVO;
import net.wanho.fitai.modules.ai.service.AiRagEvalServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiRagEvalServiceImpl extends MPJBaseServiceImpl<AiRagEvalMapper, AiRagEval> implements AiRagEvalServiceI {

    private final Converter converter;

    @Override
    public IPage<AiRagEvalVO> findByPage(AiRagEvalQueryDTO queryDto) {

        IPage<AiRagEval> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiRagEval> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getEvalName()), AiRagEval::getEvalName, queryDto.getEvalName());
        IPage<AiRagEval> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiRagEvalVO.class));

    }

    @Override
    public void add(AiRagEvalAddDTO addDto) {
        AiRagEval airageval = converter.convert(addDto, AiRagEval.class);
        this.save(airageval);
    }

    @Override
    public void update(AiRagEvalUpdateDTO updateDto) {
        AiRagEval airageval = converter.convert(updateDto, AiRagEval.class);
        this.updateById(airageval);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiRagEvalVO findById(Long id) {
        AiRagEval entity = this.getById(id);
        return converter.convert(entity, AiRagEvalVO.class);
    }
}
