package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiDatasetRowMapper;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetRowAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetRowQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetRowUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiDatasetRow;
import net.wanho.fitai.modules.ai.model.vo.AiDatasetRowVO;
import net.wanho.fitai.modules.ai.service.AiDatasetRowServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiDatasetRowServiceImpl extends MPJBaseServiceImpl<AiDatasetRowMapper, AiDatasetRow> implements AiDatasetRowServiceI {

    private final Converter converter;

    @Override
    public IPage<AiDatasetRowVO> findByPage(AiDatasetRowQueryDTO queryDto) {

        IPage<AiDatasetRow> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiDatasetRow> wrapper = new LambdaQueryWrapper<>();
        IPage<AiDatasetRow> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiDatasetRowVO.class));

    }

    @Override
    public void add(AiDatasetRowAddDTO addDto) {
        AiDatasetRow aidatasetrow = converter.convert(addDto, AiDatasetRow.class);
        this.save(aidatasetrow);
    }

    @Override
    public void update(AiDatasetRowUpdateDTO updateDto) {
        AiDatasetRow aidatasetrow = converter.convert(updateDto, AiDatasetRow.class);
        this.updateById(aidatasetrow);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiDatasetRowVO findById(Long id) {
        AiDatasetRow entity = this.getById(id);
        return converter.convert(entity, AiDatasetRowVO.class);
    }
}
