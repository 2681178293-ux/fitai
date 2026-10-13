package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdSkuMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdSku;
import net.wanho.fitai.modules.mall.model.vo.PrdSkuVO;
import net.wanho.fitai.modules.mall.service.PrdSkuServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdSkuServiceImpl extends MPJBaseServiceImpl<PrdSkuMapper, PrdSku> implements PrdSkuServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdSkuVO> findByPage(PrdSkuQueryDTO queryDto) {

        IPage<PrdSku> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSkuName()), PrdSku::getSkuName, queryDto.getSkuName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), PrdSku::getStatus, queryDto.getStatus());
        IPage<PrdSku> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdSkuVO.class));

    }

    @Override
    public void add(PrdSkuAddDTO addDto) {
        PrdSku prdsku = converter.convert(addDto, PrdSku.class);
        this.save(prdsku);
    }

    @Override
    public void update(PrdSkuUpdateDTO updateDto) {
        PrdSku prdsku = converter.convert(updateDto, PrdSku.class);
        this.updateById(prdsku);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdSkuVO findById(Long id) {
        PrdSku entity = this.getById(id);
        return converter.convert(entity, PrdSkuVO.class);
    }
}
