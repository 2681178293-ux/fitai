package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdCartMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdCartAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCartQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCartUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdCart;
import net.wanho.fitai.modules.mall.model.vo.PrdCartVO;
import net.wanho.fitai.modules.mall.service.PrdCartServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdCartServiceImpl extends MPJBaseServiceImpl<PrdCartMapper, PrdCart> implements PrdCartServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdCartVO> findByPage(PrdCartQueryDTO queryDto) {

        IPage<PrdCart> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSkuName()), PrdCart::getSkuName, queryDto.getSkuName());
        IPage<PrdCart> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdCartVO.class));

    }

    @Override
    public void add(PrdCartAddDTO addDto) {
        PrdCart prdcart = converter.convert(addDto, PrdCart.class);
        this.save(prdcart);
    }

    @Override
    public void update(PrdCartUpdateDTO updateDto) {
        PrdCart prdcart = converter.convert(updateDto, PrdCart.class);
        this.updateById(prdcart);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdCartVO findById(Long id) {
        PrdCart entity = this.getById(id);
        return converter.convert(entity, PrdCartVO.class);
    }
}
