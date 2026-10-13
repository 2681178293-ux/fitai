package net.wanho.fitai.modules.training.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.training.mapper.TrainPlanMapper;
import net.wanho.fitai.modules.training.model.dto.TrainPlanAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainPlan;
import net.wanho.fitai.modules.training.model.vo.TrainPlanVO;
import net.wanho.fitai.modules.training.service.TrainPlanServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TrainPlanServiceImpl extends MPJBaseServiceImpl<TrainPlanMapper, TrainPlan> implements TrainPlanServiceI {

    private final Converter converter;

    @Override
    public IPage<TrainPlanVO> findByPage(TrainPlanQueryDTO queryDto) {

        IPage<TrainPlan> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<TrainPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPlanName()), TrainPlan::getPlanName, queryDto.getPlanName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), TrainPlan::getStatus, queryDto.getStatus());
        IPage<TrainPlan> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, TrainPlanVO.class));

    }

    @Override
    public void add(TrainPlanAddDTO addDto) {
        TrainPlan trainplan = converter.convert(addDto, TrainPlan.class);
        this.save(trainplan);
    }

    @Override
    public void update(TrainPlanUpdateDTO updateDto) {
        TrainPlan trainplan = converter.convert(updateDto, TrainPlan.class);
        this.updateById(trainplan);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public TrainPlanVO findById(Long id) {
        TrainPlan entity = this.getById(id);
        return converter.convert(entity, TrainPlanVO.class);
    }
}
