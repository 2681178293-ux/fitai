package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiModelMapper;
import net.wanho.fitai.modules.ai.model.dto.AiModelAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiModelQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiModelUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiModel;
import net.wanho.fitai.modules.ai.model.vo.AiModelVO;
import net.wanho.fitai.modules.ai.service.AiModelServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiModelServiceImpl extends MPJBaseServiceImpl<AiModelMapper, AiModel> implements AiModelServiceI {

    private final Converter converter;

    @Override
    public IPage<AiModelVO> findByPage(AiModelQueryDTO queryDto) {

        IPage<AiModel> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getModelName()), AiModel::getModelName, queryDto.getModelName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getModelCode()), AiModel::getModelCode, queryDto.getModelCode());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getApiKeyRef()), AiModel::getApiKeyRef, queryDto.getApiKeyRef());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiModel::getStatus, queryDto.getStatus());
        IPage<AiModel> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiModelVO.class));

    }

    @Override
    public void add(AiModelAddDTO addDto) {
        AiModel aimodel = converter.convert(addDto, AiModel.class);
        this.save(aimodel);
    }

    @Override
    public void update(AiModelUpdateDTO updateDto) {
        AiModel aimodel = converter.convert(updateDto, AiModel.class);
        this.updateById(aimodel);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiModelVO findById(Long id) {
        AiModel entity = this.getById(id);
        return converter.convert(entity, AiModelVO.class);
    }
}
