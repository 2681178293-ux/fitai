package net.wanho.fitai.modules.diet.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.diet.mapper.DietPlanItemMapper;
import net.wanho.fitai.modules.diet.model.dto.DietPlanItemAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanItemQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanItemUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietPlanItem;
import net.wanho.fitai.modules.diet.model.vo.DietPlanItemVO;
import net.wanho.fitai.modules.diet.service.DietPlanItemServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DietPlanItemServiceImpl extends MPJBaseServiceImpl<DietPlanItemMapper, DietPlanItem> implements DietPlanItemServiceI {

    private final Converter converter;

    @Override
    public IPage<DietPlanItemVO> findByPage(DietPlanItemQueryDTO queryDto) {

        IPage<DietPlanItem> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<DietPlanItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getFoodName()), DietPlanItem::getFoodName, queryDto.getFoodName());
        IPage<DietPlanItem> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, DietPlanItemVO.class));

    }

    @Override
    public void add(DietPlanItemAddDTO addDto) {
        DietPlanItem dietplanitem = converter.convert(addDto, DietPlanItem.class);
        this.save(dietplanitem);
    }

    @Override
    public void update(DietPlanItemUpdateDTO updateDto) {
        DietPlanItem dietplanitem = converter.convert(updateDto, DietPlanItem.class);
        this.updateById(dietplanitem);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public DietPlanItemVO findById(Long id) {
        DietPlanItem entity = this.getById(id);
        return converter.convert(entity, DietPlanItemVO.class);
    }
}
