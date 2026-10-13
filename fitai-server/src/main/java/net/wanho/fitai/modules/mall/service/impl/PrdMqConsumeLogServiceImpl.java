package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdMqConsumeLogMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdMqConsumeLogAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdMqConsumeLogQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdMqConsumeLogUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdMqConsumeLog;
import net.wanho.fitai.modules.mall.model.vo.PrdMqConsumeLogVO;
import net.wanho.fitai.modules.mall.service.PrdMqConsumeLogServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdMqConsumeLogServiceImpl extends MPJBaseServiceImpl<PrdMqConsumeLogMapper, PrdMqConsumeLog> implements PrdMqConsumeLogServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdMqConsumeLogVO> findByPage(PrdMqConsumeLogQueryDTO queryDto) {

        IPage<PrdMqConsumeLog> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdMqConsumeLog> wrapper = new LambdaQueryWrapper<>();
        IPage<PrdMqConsumeLog> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdMqConsumeLogVO.class));

    }

    @Override
    public void add(PrdMqConsumeLogAddDTO addDto) {
        PrdMqConsumeLog prdmqconsumelog = converter.convert(addDto, PrdMqConsumeLog.class);
        this.save(prdmqconsumelog);
    }

    @Override
    public void update(PrdMqConsumeLogUpdateDTO updateDto) {
        PrdMqConsumeLog prdmqconsumelog = converter.convert(updateDto, PrdMqConsumeLog.class);
        this.updateById(prdmqconsumelog);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdMqConsumeLogVO findById(String id) {
        PrdMqConsumeLog entity = this.getById(id);
        return converter.convert(entity, PrdMqConsumeLogVO.class);
    }
}
