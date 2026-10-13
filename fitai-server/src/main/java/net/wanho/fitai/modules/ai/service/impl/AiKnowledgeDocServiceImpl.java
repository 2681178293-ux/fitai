package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiKnowledgeDocMapper;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeDocAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeDocQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeDocUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiKnowledgeDoc;
import net.wanho.fitai.modules.ai.model.vo.AiKnowledgeDocVO;
import net.wanho.fitai.modules.ai.service.AiKnowledgeDocServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiKnowledgeDocServiceImpl extends MPJBaseServiceImpl<AiKnowledgeDocMapper, AiKnowledgeDoc> implements AiKnowledgeDocServiceI {

    private final Converter converter;

    @Override
    public IPage<AiKnowledgeDocVO> findByPage(AiKnowledgeDocQueryDTO queryDto) {

        IPage<AiKnowledgeDoc> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiKnowledgeDoc> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getDocTitle()), AiKnowledgeDoc::getDocTitle, queryDto.getDocTitle());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiKnowledgeDoc::getStatus, queryDto.getStatus());
        IPage<AiKnowledgeDoc> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiKnowledgeDocVO.class));

    }

    @Override
    public void add(AiKnowledgeDocAddDTO addDto) {
        AiKnowledgeDoc aiknowledgedoc = converter.convert(addDto, AiKnowledgeDoc.class);
        this.save(aiknowledgedoc);
    }

    @Override
    public void update(AiKnowledgeDocUpdateDTO updateDto) {
        AiKnowledgeDoc aiknowledgedoc = converter.convert(updateDto, AiKnowledgeDoc.class);
        this.updateById(aiknowledgedoc);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiKnowledgeDocVO findById(Long id) {
        AiKnowledgeDoc entity = this.getById(id);
        return converter.convert(entity, AiKnowledgeDocVO.class);
    }
}
