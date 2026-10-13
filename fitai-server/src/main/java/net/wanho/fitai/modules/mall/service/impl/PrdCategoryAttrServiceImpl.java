package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdCategoryAttrMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAttrAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAttrQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAttrUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdCategoryAttr;
import net.wanho.fitai.modules.mall.model.vo.PrdCategoryAttrVO;
import net.wanho.fitai.modules.mall.service.PrdCategoryAttrServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdCategoryAttrServiceImpl extends MPJBaseServiceImpl<PrdCategoryAttrMapper, PrdCategoryAttr> implements PrdCategoryAttrServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdCategoryAttrVO> findByPage(PrdCategoryAttrQueryDTO queryDto) {

        IPage<PrdCategoryAttr> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdCategoryAttr> wrapper = new LambdaQueryWrapper<>();
        IPage<PrdCategoryAttr> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdCategoryAttrVO.class));

    }

    @Override
    public void add(PrdCategoryAttrAddDTO addDto) {
        PrdCategoryAttr prdcategoryattr = converter.convert(addDto, PrdCategoryAttr.class);
        this.save(prdcategoryattr);
    }

    @Override
    public void update(PrdCategoryAttrUpdateDTO updateDto) {
        PrdCategoryAttr prdcategoryattr = converter.convert(updateDto, PrdCategoryAttr.class);
        this.updateById(prdcategoryattr);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdCategoryAttrVO findById(Long id) {
        PrdCategoryAttr entity = this.getById(id);
        return converter.convert(entity, PrdCategoryAttrVO.class);
    }
}
