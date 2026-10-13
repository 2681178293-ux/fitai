package net.wanho.fitai.modules.diet.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.diet.mapper.DietCategoryMapper;
import net.wanho.fitai.modules.diet.model.dto.DietCategoryAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietCategoryQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietCategoryUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietCategory;
import net.wanho.fitai.modules.diet.model.vo.DietCategoryVO;
import net.wanho.fitai.modules.diet.service.DietCategoryServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DietCategoryServiceImpl extends MPJBaseServiceImpl<DietCategoryMapper, DietCategory> implements DietCategoryServiceI {

    private final Converter converter;

    @Override
    public IPage<DietCategoryVO> findByPage(DietCategoryQueryDTO queryDto) {

        IPage<DietCategory> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<DietCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCategoryName()), DietCategory::getCategoryName, queryDto.getCategoryName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), DietCategory::getStatus, queryDto.getStatus());
        IPage<DietCategory> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, DietCategoryVO.class));

    }

    @Override
    public void add(DietCategoryAddDTO addDto) {
        DietCategory dietcategory = converter.convert(addDto, DietCategory.class);
        this.save(dietcategory);
    }

    @Override
    public void update(DietCategoryUpdateDTO updateDto) {
        DietCategory dietcategory = converter.convert(updateDto, DietCategory.class);
        this.updateById(dietcategory);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public DietCategoryVO findById(Long id) {
        DietCategory entity = this.getById(id);
        return converter.convert(entity, DietCategoryVO.class);
    }
}
