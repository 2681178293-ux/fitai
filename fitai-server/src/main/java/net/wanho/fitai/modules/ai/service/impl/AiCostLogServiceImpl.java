package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiCostLogMapper;
import net.wanho.fitai.modules.ai.model.dto.AiCostLogAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiCostLogQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiCostLogUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiCostLog;
import net.wanho.fitai.modules.ai.model.vo.AiCostLogVO;
import net.wanho.fitai.modules.ai.service.AiCostLogServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiCostLogServiceImpl extends MPJBaseServiceImpl<AiCostLogMapper, AiCostLog> implements AiCostLogServiceI {

    private final Converter converter;

    @Override
    public IPage<AiCostLogVO> findByPage(AiCostLogQueryDTO queryDto) {

        IPage<AiCostLog> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiCostLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getModelCode()), AiCostLog::getModelCode, queryDto.getModelCode());
        IPage<AiCostLog> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiCostLogVO.class));

    }

    @Override
    public void add(AiCostLogAddDTO addDto) {
        AiCostLog aicostlog = converter.convert(addDto, AiCostLog.class);
        this.save(aicostlog);
    }

    @Override
    public void update(AiCostLogUpdateDTO updateDto) {
        AiCostLog aicostlog = converter.convert(updateDto, AiCostLog.class);
        this.updateById(aicostlog);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiCostLogVO findById(Long id) {
        AiCostLog entity = this.getById(id);
        return converter.convert(entity, AiCostLogVO.class);
    }
}
