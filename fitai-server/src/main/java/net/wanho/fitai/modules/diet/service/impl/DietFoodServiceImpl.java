package net.wanho.fitai.modules.diet.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.diet.mapper.DietFoodMapper;
import net.wanho.fitai.modules.diet.model.dto.DietFoodAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietFoodQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietFoodUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietFood;
import net.wanho.fitai.modules.diet.model.vo.DietFoodVO;
import net.wanho.fitai.modules.diet.service.DietFoodServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DietFoodServiceImpl extends MPJBaseServiceImpl<DietFoodMapper, DietFood> implements DietFoodServiceI {

    private final Converter converter;

    @Override
    public IPage<DietFoodVO> findByPage(DietFoodQueryDTO queryDto) {

        IPage<DietFood> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<DietFood> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCategoryName()), DietFood::getCategoryName, queryDto.getCategoryName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getFoodName()), DietFood::getFoodName, queryDto.getFoodName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getFoodCode()), DietFood::getFoodCode, queryDto.getFoodCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), DietFood::getStatus, queryDto.getStatus());
        IPage<DietFood> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, DietFoodVO.class));

    }

    @Override
    public void add(DietFoodAddDTO addDto) {
        DietFood dietfood = converter.convert(addDto, DietFood.class);
        this.save(dietfood);
    }

    @Override
    public void update(DietFoodUpdateDTO updateDto) {
        DietFood dietfood = converter.convert(updateDto, DietFood.class);
        this.updateById(dietfood);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public DietFoodVO findById(Long id) {
        DietFood entity = this.getById(id);
        return converter.convert(entity, DietFoodVO.class);
    }
}
