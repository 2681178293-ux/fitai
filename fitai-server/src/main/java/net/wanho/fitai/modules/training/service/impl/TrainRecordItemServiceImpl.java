package net.wanho.fitai.modules.training.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.training.mapper.TrainRecordItemMapper;
import net.wanho.fitai.modules.training.model.dto.TrainRecordItemAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordItemQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordItemUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainRecordItem;
import net.wanho.fitai.modules.training.model.vo.TrainRecordItemVO;
import net.wanho.fitai.modules.training.service.TrainRecordItemServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TrainRecordItemServiceImpl extends MPJBaseServiceImpl<TrainRecordItemMapper, TrainRecordItem> implements TrainRecordItemServiceI {

    private final Converter converter;

    @Override
    public IPage<TrainRecordItemVO> findByPage(TrainRecordItemQueryDTO queryDto) {

        IPage<TrainRecordItem> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<TrainRecordItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getActionName()), TrainRecordItem::getActionName, queryDto.getActionName());
        IPage<TrainRecordItem> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, TrainRecordItemVO.class));

    }

    @Override
    public void add(TrainRecordItemAddDTO addDto) {
        TrainRecordItem trainrecorditem = converter.convert(addDto, TrainRecordItem.class);
        this.save(trainrecorditem);
    }

    @Override
    public void update(TrainRecordItemUpdateDTO updateDto) {
        TrainRecordItem trainrecorditem = converter.convert(updateDto, TrainRecordItem.class);
        this.updateById(trainrecorditem);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public TrainRecordItemVO findById(Long id) {
        TrainRecordItem entity = this.getById(id);
        return converter.convert(entity, TrainRecordItemVO.class);
    }
}
