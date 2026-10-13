package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.MktFlashSaleMapper;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktFlashSale;
import net.wanho.fitai.modules.marketing.model.vo.MktFlashSaleVO;
import net.wanho.fitai.modules.marketing.service.MktFlashSaleServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MktFlashSaleServiceImpl extends MPJBaseServiceImpl<MktFlashSaleMapper, MktFlashSale> implements MktFlashSaleServiceI {

    private final Converter converter;

    @Override
    public IPage<MktFlashSaleVO> findByPage(MktFlashSaleQueryDTO queryDto) {

        IPage<MktFlashSale> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MktFlashSale> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getActivityName()), MktFlashSale::getActivityName, queryDto.getActivityName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), MktFlashSale::getStatus, queryDto.getStatus());
        IPage<MktFlashSale> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MktFlashSaleVO.class));

    }

    @Override
    public void add(MktFlashSaleAddDTO addDto) {
        MktFlashSale mktflashsale = converter.convert(addDto, MktFlashSale.class);
        this.save(mktflashsale);
    }

    @Override
    public void update(MktFlashSaleUpdateDTO updateDto) {
        MktFlashSale mktflashsale = converter.convert(updateDto, MktFlashSale.class);
        this.updateById(mktflashsale);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MktFlashSaleVO findById(Long id) {
        MktFlashSale entity = this.getById(id);
        return converter.convert(entity, MktFlashSaleVO.class);
    }
}
