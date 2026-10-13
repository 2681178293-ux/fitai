package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.MktMqConsumeLogMapper;
import net.wanho.fitai.modules.marketing.model.dto.MktMqConsumeLogAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktMqConsumeLogQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktMqConsumeLogUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktMqConsumeLog;
import net.wanho.fitai.modules.marketing.model.vo.MktMqConsumeLogVO;
import net.wanho.fitai.modules.marketing.service.MktMqConsumeLogServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MktMqConsumeLogServiceImpl extends MPJBaseServiceImpl<MktMqConsumeLogMapper, MktMqConsumeLog> implements MktMqConsumeLogServiceI {

    private final Converter converter;

    @Override
    public IPage<MktMqConsumeLogVO> findByPage(MktMqConsumeLogQueryDTO queryDto) {

        IPage<MktMqConsumeLog> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MktMqConsumeLog> wrapper = new LambdaQueryWrapper<>();
        IPage<MktMqConsumeLog> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MktMqConsumeLogVO.class));

    }

    @Override
    public void add(MktMqConsumeLogAddDTO addDto) {
        MktMqConsumeLog mktmqconsumelog = converter.convert(addDto, MktMqConsumeLog.class);
        this.save(mktmqconsumelog);
    }

    @Override
    public void update(MktMqConsumeLogUpdateDTO updateDto) {
        MktMqConsumeLog mktmqconsumelog = converter.convert(updateDto, MktMqConsumeLog.class);
        this.updateById(mktmqconsumelog);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MktMqConsumeLogVO findById(String id) {
        MktMqConsumeLog entity = this.getById(id);
        return converter.convert(entity, MktMqConsumeLogVO.class);
    }
}
