package net.wanho.fitai.modules.training.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.training.mapper.TrainDeviceMapper;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainDevice;
import net.wanho.fitai.modules.training.model.vo.TrainDeviceVO;
import net.wanho.fitai.modules.training.service.TrainDeviceServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TrainDeviceServiceImpl extends MPJBaseServiceImpl<TrainDeviceMapper, TrainDevice> implements TrainDeviceServiceI {

    private final Converter converter;

    @Override
    public IPage<TrainDeviceVO> findByPage(TrainDeviceQueryDTO queryDto) {

        IPage<TrainDevice> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<TrainDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getDeviceName()), TrainDevice::getDeviceName, queryDto.getDeviceName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), TrainDevice::getStatus, queryDto.getStatus());
        IPage<TrainDevice> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, TrainDeviceVO.class));

    }

    @Override
    public void add(TrainDeviceAddDTO addDto) {
        TrainDevice traindevice = converter.convert(addDto, TrainDevice.class);
        this.save(traindevice);
    }

    @Override
    public void update(TrainDeviceUpdateDTO updateDto) {
        TrainDevice traindevice = converter.convert(updateDto, TrainDevice.class);
        this.updateById(traindevice);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public TrainDeviceVO findById(Long id) {
        TrainDevice entity = this.getById(id);
        return converter.convert(entity, TrainDeviceVO.class);
    }
}
