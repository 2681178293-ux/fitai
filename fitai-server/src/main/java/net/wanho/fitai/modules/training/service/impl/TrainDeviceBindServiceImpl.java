package net.wanho.fitai.modules.training.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.training.mapper.TrainDeviceBindMapper;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceBindAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceBindQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceBindUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainDeviceBind;
import net.wanho.fitai.modules.training.model.vo.TrainDeviceBindVO;
import net.wanho.fitai.modules.training.service.TrainDeviceBindServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TrainDeviceBindServiceImpl extends MPJBaseServiceImpl<TrainDeviceBindMapper, TrainDeviceBind> implements TrainDeviceBindServiceI {

    private final Converter converter;

    @Override
    public IPage<TrainDeviceBindVO> findByPage(TrainDeviceBindQueryDTO queryDto) {

        IPage<TrainDeviceBind> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<TrainDeviceBind> wrapper = new LambdaQueryWrapper<>();
        IPage<TrainDeviceBind> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, TrainDeviceBindVO.class));

    }

    @Override
    public void add(TrainDeviceBindAddDTO addDto) {
        TrainDeviceBind traindevicebind = converter.convert(addDto, TrainDeviceBind.class);
        this.save(traindevicebind);
    }

    @Override
    public void update(TrainDeviceBindUpdateDTO updateDto) {
        TrainDeviceBind traindevicebind = converter.convert(updateDto, TrainDeviceBind.class);
        this.updateById(traindevicebind);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public TrainDeviceBindVO findById(Long id) {
        TrainDeviceBind entity = this.getById(id);
        return converter.convert(entity, TrainDeviceBindVO.class);
    }
}
