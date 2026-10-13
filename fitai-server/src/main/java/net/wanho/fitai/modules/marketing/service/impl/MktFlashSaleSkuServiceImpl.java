package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.MktFlashSaleSkuMapper;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleSkuAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleSkuQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleSkuUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktFlashSaleSku;
import net.wanho.fitai.modules.marketing.model.vo.MktFlashSaleSkuVO;
import net.wanho.fitai.modules.marketing.service.MktFlashSaleSkuServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MktFlashSaleSkuServiceImpl extends MPJBaseServiceImpl<MktFlashSaleSkuMapper, MktFlashSaleSku> implements MktFlashSaleSkuServiceI {

    private final Converter converter;

    @Override
    public IPage<MktFlashSaleSkuVO> findByPage(MktFlashSaleSkuQueryDTO queryDto) {

        IPage<MktFlashSaleSku> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MktFlashSaleSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSkuName()), MktFlashSaleSku::getSkuName, queryDto.getSkuName());
        IPage<MktFlashSaleSku> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MktFlashSaleSkuVO.class));

    }

    @Override
    public void add(MktFlashSaleSkuAddDTO addDto) {
        MktFlashSaleSku mktflashsalesku = converter.convert(addDto, MktFlashSaleSku.class);
        this.save(mktflashsalesku);
    }

    @Override
    public void update(MktFlashSaleSkuUpdateDTO updateDto) {
        MktFlashSaleSku mktflashsalesku = converter.convert(updateDto, MktFlashSaleSku.class);
        this.updateById(mktflashsalesku);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MktFlashSaleSkuVO findById(Long id) {
        MktFlashSaleSku entity = this.getById(id);
        return converter.convert(entity, MktFlashSaleSkuVO.class);
    }
}
