package net.wanho.fitai.modules.training.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.training.mapper.TrainPlanItemMapper;
import net.wanho.fitai.modules.training.model.dto.TrainPlanItemAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanItemQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanItemUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainPlanItem;
import net.wanho.fitai.modules.training.model.vo.TrainPlanItemVO;
import net.wanho.fitai.modules.training.service.TrainPlanItemServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TrainPlanItemServiceImpl extends MPJBaseServiceImpl<TrainPlanItemMapper, TrainPlanItem> implements TrainPlanItemServiceI {

    private final Converter converter;

    @Override
    public IPage<TrainPlanItemVO> findByPage(TrainPlanItemQueryDTO queryDto) {

        IPage<TrainPlanItem> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<TrainPlanItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getActionName()), TrainPlanItem::getActionName, queryDto.getActionName());
        IPage<TrainPlanItem> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, TrainPlanItemVO.class));

    }

    @Override
    public void add(TrainPlanItemAddDTO addDto) {
        TrainPlanItem trainplanitem = converter.convert(addDto, TrainPlanItem.class);
        this.save(trainplanitem);
    }

    @Override
    public void update(TrainPlanItemUpdateDTO updateDto) {
        TrainPlanItem trainplanitem = converter.convert(updateDto, TrainPlanItem.class);
        this.updateById(trainplanitem);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public TrainPlanItemVO findById(Long id) {
        TrainPlanItem entity = this.getById(id);
        return converter.convert(entity, TrainPlanItemVO.class);
    }
}
