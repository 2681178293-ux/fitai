package net.wanho.fitai.modules.diet.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.diet.mapper.DietPlanMapper;
import net.wanho.fitai.modules.diet.model.dto.DietPlanAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietPlan;
import net.wanho.fitai.modules.diet.model.vo.DietPlanVO;
import net.wanho.fitai.modules.diet.service.DietPlanServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DietPlanServiceImpl extends MPJBaseServiceImpl<DietPlanMapper, DietPlan> implements DietPlanServiceI {

    private final Converter converter;

    @Override
    public IPage<DietPlanVO> findByPage(DietPlanQueryDTO queryDto) {

        IPage<DietPlan> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<DietPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPlanName()), DietPlan::getPlanName, queryDto.getPlanName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), DietPlan::getStatus, queryDto.getStatus());
        IPage<DietPlan> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, DietPlanVO.class));

    }

    @Override
    public void add(DietPlanAddDTO addDto) {
        DietPlan dietplan = converter.convert(addDto, DietPlan.class);
        this.save(dietplan);
    }

    @Override
    public void update(DietPlanUpdateDTO updateDto) {
        DietPlan dietplan = converter.convert(updateDto, DietPlan.class);
        this.updateById(dietplan);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public DietPlanVO findById(Long id) {
        DietPlan entity = this.getById(id);
        return converter.convert(entity, DietPlanVO.class);
    }
}
