package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.MktCouponUserMapper;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUserAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUserQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUserUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktCouponUser;
import net.wanho.fitai.modules.marketing.model.vo.MktCouponUserVO;
import net.wanho.fitai.modules.marketing.service.MktCouponUserServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MktCouponUserServiceImpl extends MPJBaseServiceImpl<MktCouponUserMapper, MktCouponUser> implements MktCouponUserServiceI {

    private final Converter converter;

    @Override
    public IPage<MktCouponUserVO> findByPage(MktCouponUserQueryDTO queryDto) {

        IPage<MktCouponUser> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MktCouponUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOrderNo()), MktCouponUser::getOrderNo, queryDto.getOrderNo());
        IPage<MktCouponUser> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MktCouponUserVO.class));

    }

    @Override
    public void add(MktCouponUserAddDTO addDto) {
        MktCouponUser mktcouponuser = converter.convert(addDto, MktCouponUser.class);
        this.save(mktcouponuser);
    }

    @Override
    public void update(MktCouponUserUpdateDTO updateDto) {
        MktCouponUser mktcouponuser = converter.convert(updateDto, MktCouponUser.class);
        this.updateById(mktcouponuser);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MktCouponUserVO findById(Long id) {
        MktCouponUser entity = this.getById(id);
        return converter.convert(entity, MktCouponUserVO.class);
    }
}
