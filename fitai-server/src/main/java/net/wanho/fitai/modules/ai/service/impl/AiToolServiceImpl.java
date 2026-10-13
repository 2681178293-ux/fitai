package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiToolMapper;
import net.wanho.fitai.modules.ai.model.dto.AiToolAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiToolQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiToolUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiTool;
import net.wanho.fitai.modules.ai.model.vo.AiToolVO;
import net.wanho.fitai.modules.ai.service.AiToolServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiToolServiceImpl extends MPJBaseServiceImpl<AiToolMapper, AiTool> implements AiToolServiceI {

    private final Converter converter;

    @Override
    public IPage<AiToolVO> findByPage(AiToolQueryDTO queryDto) {

        IPage<AiTool> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiTool> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getToolName()), AiTool::getToolName, queryDto.getToolName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getToolCode()), AiTool::getToolCode, queryDto.getToolCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiTool::getStatus, queryDto.getStatus());
        IPage<AiTool> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiToolVO.class));

    }

    @Override
    public void add(AiToolAddDTO addDto) {
        AiTool aitool = converter.convert(addDto, AiTool.class);
        this.save(aitool);
    }

    @Override
    public void update(AiToolUpdateDTO updateDto) {
        AiTool aitool = converter.convert(updateDto, AiTool.class);
        this.updateById(aitool);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiToolVO findById(Long id) {
        AiTool entity = this.getById(id);
        return converter.convert(entity, AiToolVO.class);
    }
}
