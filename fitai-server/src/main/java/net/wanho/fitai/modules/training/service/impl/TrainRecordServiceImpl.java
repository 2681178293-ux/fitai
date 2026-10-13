package net.wanho.fitai.modules.training.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.training.mapper.TrainRecordMapper;
import net.wanho.fitai.modules.training.model.dto.TrainRecordAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainRecord;
import net.wanho.fitai.modules.training.model.vo.TrainRecordVO;
import net.wanho.fitai.modules.training.service.TrainRecordServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TrainRecordServiceImpl extends MPJBaseServiceImpl<TrainRecordMapper, TrainRecord> implements TrainRecordServiceI {

    private final Converter converter;

    @Override
    public IPage<TrainRecordVO> findByPage(TrainRecordQueryDTO queryDto) {

        IPage<TrainRecord> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<TrainRecord> wrapper = new LambdaQueryWrapper<>();
        IPage<TrainRecord> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, TrainRecordVO.class));

    }

    @Override
    public void add(TrainRecordAddDTO addDto) {
        TrainRecord trainrecord = converter.convert(addDto, TrainRecord.class);
        this.save(trainrecord);
    }

    @Override
    public void update(TrainRecordUpdateDTO updateDto) {
        TrainRecord trainrecord = converter.convert(updateDto, TrainRecord.class);
        this.updateById(trainrecord);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public TrainRecordVO findById(Long id) {
        TrainRecord entity = this.getById(id);
        return converter.convert(entity, TrainRecordVO.class);
    }
}
