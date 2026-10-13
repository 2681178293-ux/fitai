package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdStockMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdStockAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdStockQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdStockUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdStock;
import net.wanho.fitai.modules.mall.model.vo.PrdStockVO;
import net.wanho.fitai.modules.mall.service.PrdStockServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdStockServiceImpl extends MPJBaseServiceImpl<PrdStockMapper, PrdStock> implements PrdStockServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdStockVO> findByPage(PrdStockQueryDTO queryDto) {

        IPage<PrdStock> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getWarehouseCode()), PrdStock::getWarehouseCode, queryDto.getWarehouseCode());
        IPage<PrdStock> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdStockVO.class));

    }

    @Override
    public void add(PrdStockAddDTO addDto) {
        PrdStock prdstock = converter.convert(addDto, PrdStock.class);
        this.save(prdstock);
    }

    @Override
    public void update(PrdStockUpdateDTO updateDto) {
        PrdStock prdstock = converter.convert(updateDto, PrdStock.class);
        this.updateById(prdstock);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdStockVO findById(Long id) {
        PrdStock entity = this.getById(id);
        return converter.convert(entity, PrdStockVO.class);
    }
}
