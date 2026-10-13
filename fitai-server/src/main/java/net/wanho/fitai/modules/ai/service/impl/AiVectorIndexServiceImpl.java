package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiVectorIndexMapper;
import net.wanho.fitai.modules.ai.model.dto.AiVectorIndexAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiVectorIndexQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiVectorIndexUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiVectorIndex;
import net.wanho.fitai.modules.ai.model.vo.AiVectorIndexVO;
import net.wanho.fitai.modules.ai.service.AiVectorIndexServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiVectorIndexServiceImpl extends MPJBaseServiceImpl<AiVectorIndexMapper, AiVectorIndex> implements AiVectorIndexServiceI {

    private final Converter converter;

    @Override
    public IPage<AiVectorIndexVO> findByPage(AiVectorIndexQueryDTO queryDto) {

        IPage<AiVectorIndex> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiVectorIndex> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getIndexName()), AiVectorIndex::getIndexName, queryDto.getIndexName());
        IPage<AiVectorIndex> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiVectorIndexVO.class));

    }

    @Override
    public void add(AiVectorIndexAddDTO addDto) {
        AiVectorIndex aivectorindex = converter.convert(addDto, AiVectorIndex.class);
        this.save(aivectorindex);
    }

    @Override
    public void update(AiVectorIndexUpdateDTO updateDto) {
        AiVectorIndex aivectorindex = converter.convert(updateDto, AiVectorIndex.class);
        this.updateById(aivectorindex);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiVectorIndexVO findById(Long id) {
        AiVectorIndex entity = this.getById(id);
        return converter.convert(entity, AiVectorIndexVO.class);
    }
}
