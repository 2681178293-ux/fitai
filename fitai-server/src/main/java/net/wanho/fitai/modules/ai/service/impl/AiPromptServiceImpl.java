package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiPromptMapper;
import net.wanho.fitai.modules.ai.model.dto.AiPromptAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPromptQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPromptUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiPrompt;
import net.wanho.fitai.modules.ai.model.vo.AiPromptVO;
import net.wanho.fitai.modules.ai.service.AiPromptServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiPromptServiceImpl extends MPJBaseServiceImpl<AiPromptMapper, AiPrompt> implements AiPromptServiceI {

    private final Converter converter;

    @Override
    public IPage<AiPromptVO> findByPage(AiPromptQueryDTO queryDto) {

        IPage<AiPrompt> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiPrompt> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPromptName()), AiPrompt::getPromptName, queryDto.getPromptName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPromptCode()), AiPrompt::getPromptCode, queryDto.getPromptCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiPrompt::getStatus, queryDto.getStatus());
        IPage<AiPrompt> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiPromptVO.class));

    }

    @Override
    public void add(AiPromptAddDTO addDto) {
        AiPrompt aiprompt = converter.convert(addDto, AiPrompt.class);
        this.save(aiprompt);
    }

    @Override
    public void update(AiPromptUpdateDTO updateDto) {
        AiPrompt aiprompt = converter.convert(updateDto, AiPrompt.class);
        this.updateById(aiprompt);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiPromptVO findById(Long id) {
        AiPrompt entity = this.getById(id);
        return converter.convert(entity, AiPromptVO.class);
    }
}
