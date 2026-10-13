package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemGrowthLogMapper;
import net.wanho.fitai.modules.member.model.dto.MemGrowthLogAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemGrowthLogQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemGrowthLogUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemGrowthLog;
import net.wanho.fitai.modules.member.model.vo.MemGrowthLogVO;
import net.wanho.fitai.modules.member.service.MemGrowthLogServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemGrowthLogServiceImpl extends MPJBaseServiceImpl<MemGrowthLogMapper, MemGrowthLog> implements MemGrowthLogServiceI {

    private final Converter converter;

    @Override
    public IPage<MemGrowthLogVO> findByPage(MemGrowthLogQueryDTO queryDto) {

        IPage<MemGrowthLog> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemGrowthLog> wrapper = new LambdaQueryWrapper<>();
        IPage<MemGrowthLog> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemGrowthLogVO.class));

    }

    @Override
    public void add(MemGrowthLogAddDTO addDto) {
        MemGrowthLog memgrowthlog = converter.convert(addDto, MemGrowthLog.class);
        this.save(memgrowthlog);
    }

    @Override
    public void update(MemGrowthLogUpdateDTO updateDto) {
        MemGrowthLog memgrowthlog = converter.convert(updateDto, MemGrowthLog.class);
        this.updateById(memgrowthlog);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemGrowthLogVO findById(Long id) {
        MemGrowthLog entity = this.getById(id);
        return converter.convert(entity, MemGrowthLogVO.class);
    }
}
