package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.MktCouponMapper;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktCoupon;
import net.wanho.fitai.modules.marketing.model.vo.MktCouponVO;
import net.wanho.fitai.modules.marketing.service.MktCouponServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MktCouponServiceImpl extends MPJBaseServiceImpl<MktCouponMapper, MktCoupon> implements MktCouponServiceI {

    private final Converter converter;

    @Override
    public IPage<MktCouponVO> findByPage(MktCouponQueryDTO queryDto) {

        IPage<MktCoupon> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MktCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getName()), MktCoupon::getName, queryDto.getName());
        IPage<MktCoupon> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MktCouponVO.class));

    }

    @Override
    public void add(MktCouponAddDTO addDto) {
        MktCoupon mktcoupon = converter.convert(addDto, MktCoupon.class);
        this.save(mktcoupon);
    }

    @Override
    public void update(MktCouponUpdateDTO updateDto) {
        MktCoupon mktcoupon = converter.convert(updateDto, MktCoupon.class);
        this.updateById(mktcoupon);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MktCouponVO findById(Long id) {
        MktCoupon entity = this.getById(id);
        return converter.convert(entity, MktCouponVO.class);
    }
}
