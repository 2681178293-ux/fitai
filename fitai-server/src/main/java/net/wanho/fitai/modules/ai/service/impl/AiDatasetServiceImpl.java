package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiDatasetMapper;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiDataset;
import net.wanho.fitai.modules.ai.model.vo.AiDatasetVO;
import net.wanho.fitai.modules.ai.service.AiDatasetServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiDatasetServiceImpl extends MPJBaseServiceImpl<AiDatasetMapper, AiDataset> implements AiDatasetServiceI {

    private final Converter converter;

    @Override
    public IPage<AiDatasetVO> findByPage(AiDatasetQueryDTO queryDto) {

        IPage<AiDataset> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getDatasetName()), AiDataset::getDatasetName, queryDto.getDatasetName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getDatasetCode()), AiDataset::getDatasetCode, queryDto.getDatasetCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiDataset::getStatus, queryDto.getStatus());
        IPage<AiDataset> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiDatasetVO.class));

    }

    @Override
    public void add(AiDatasetAddDTO addDto) {
        AiDataset aidataset = converter.convert(addDto, AiDataset.class);
        this.save(aidataset);
    }

    @Override
    public void update(AiDatasetUpdateDTO updateDto) {
        AiDataset aidataset = converter.convert(updateDto, AiDataset.class);
        this.updateById(aidataset);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiDatasetVO findById(Long id) {
        AiDataset entity = this.getById(id);
        return converter.convert(entity, AiDatasetVO.class);
    }
}
