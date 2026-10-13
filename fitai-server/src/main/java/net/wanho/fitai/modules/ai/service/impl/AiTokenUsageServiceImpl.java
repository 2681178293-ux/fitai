package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiTokenUsageMapper;
import net.wanho.fitai.modules.ai.model.dto.AiTokenUsageAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTokenUsageQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTokenUsageUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiTokenUsage;
import net.wanho.fitai.modules.ai.model.vo.AiTokenUsageVO;
import net.wanho.fitai.modules.ai.service.AiTokenUsageServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiTokenUsageServiceImpl extends MPJBaseServiceImpl<AiTokenUsageMapper, AiTokenUsage> implements AiTokenUsageServiceI {

    private final Converter converter;

    @Override
    public IPage<AiTokenUsageVO> findByPage(AiTokenUsageQueryDTO queryDto) {

        IPage<AiTokenUsage> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiTokenUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getModelCode()), AiTokenUsage::getModelCode, queryDto.getModelCode());
        IPage<AiTokenUsage> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiTokenUsageVO.class));

    }

    @Override
    public void add(AiTokenUsageAddDTO addDto) {
        AiTokenUsage aitokenusage = converter.convert(addDto, AiTokenUsage.class);
        this.save(aitokenusage);
    }

    @Override
    public void update(AiTokenUsageUpdateDTO updateDto) {
        AiTokenUsage aitokenusage = converter.convert(updateDto, AiTokenUsage.class);
        this.updateById(aitokenusage);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiTokenUsageVO findById(Long id) {
        AiTokenUsage entity = this.getById(id);
        return converter.convert(entity, AiTokenUsageVO.class);
    }
}
