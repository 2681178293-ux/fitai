package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdSkuAttrMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAttrAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAttrQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAttrUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdSkuAttr;
import net.wanho.fitai.modules.mall.model.vo.PrdSkuAttrVO;
import net.wanho.fitai.modules.mall.service.PrdSkuAttrServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdSkuAttrServiceImpl extends MPJBaseServiceImpl<PrdSkuAttrMapper, PrdSkuAttr> implements PrdSkuAttrServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdSkuAttrVO> findByPage(PrdSkuAttrQueryDTO queryDto) {

        IPage<PrdSkuAttr> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdSkuAttr> wrapper = new LambdaQueryWrapper<>();
        IPage<PrdSkuAttr> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdSkuAttrVO.class));

    }

    @Override
    public void add(PrdSkuAttrAddDTO addDto) {
        PrdSkuAttr prdskuattr = converter.convert(addDto, PrdSkuAttr.class);
        this.save(prdskuattr);
    }

    @Override
    public void update(PrdSkuAttrUpdateDTO updateDto) {
        PrdSkuAttr prdskuattr = converter.convert(updateDto, PrdSkuAttr.class);
        this.updateById(prdskuattr);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdSkuAttrVO findById(Long id) {
        PrdSkuAttr entity = this.getById(id);
        return converter.convert(entity, PrdSkuAttrVO.class);
    }
}
