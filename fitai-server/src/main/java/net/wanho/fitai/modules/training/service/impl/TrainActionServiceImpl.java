package net.wanho.fitai.modules.training.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.training.mapper.TrainActionMapper;
import net.wanho.fitai.modules.training.model.dto.TrainActionAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainActionQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainActionUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainAction;
import net.wanho.fitai.modules.training.model.vo.TrainActionVO;
import net.wanho.fitai.modules.training.service.TrainActionServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TrainActionServiceImpl extends MPJBaseServiceImpl<TrainActionMapper, TrainAction> implements TrainActionServiceI {

    private final Converter converter;

    @Override
    public IPage<TrainActionVO> findByPage(TrainActionQueryDTO queryDto) {

        IPage<TrainAction> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<TrainAction> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getActionName()), TrainAction::getActionName, queryDto.getActionName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getActionCode()), TrainAction::getActionCode, queryDto.getActionCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), TrainAction::getStatus, queryDto.getStatus());
        IPage<TrainAction> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, TrainActionVO.class));

    }

    @Override
    public void add(TrainActionAddDTO addDto) {
        TrainAction trainaction = converter.convert(addDto, TrainAction.class);
        this.save(trainaction);
    }

    @Override
    public void update(TrainActionUpdateDTO updateDto) {
        TrainAction trainaction = converter.convert(updateDto, TrainAction.class);
        this.updateById(trainaction);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public TrainActionVO findById(Long id) {
        TrainAction entity = this.getById(id);
        return converter.convert(entity, TrainActionVO.class);
    }
}
