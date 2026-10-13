package net.wanho.fitai.modules.coach.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.coach.mapper.UsrCoachBookingMapper;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBookingAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBookingQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBookingUpdateDTO;
import net.wanho.fitai.modules.coach.model.po.UsrCoachBooking;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachBookingVO;
import net.wanho.fitai.modules.coach.service.UsrCoachBookingServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsrCoachBookingServiceImpl extends MPJBaseServiceImpl<UsrCoachBookingMapper, UsrCoachBooking> implements UsrCoachBookingServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrCoachBookingVO> findByPage(UsrCoachBookingQueryDTO queryDto) {

        IPage<UsrCoachBooking> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<UsrCoachBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCoachName()), UsrCoachBooking::getCoachName, queryDto.getCoachName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getCustomerId()), UsrCoachBooking::getCustomerId, queryDto.getCustomerId());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), UsrCoachBooking::getStatus, queryDto.getStatus());
        IPage<UsrCoachBooking> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, UsrCoachBookingVO.class));

    }

    @Override
    public void add(UsrCoachBookingAddDTO addDto) {
        UsrCoachBooking usrcoachbooking = converter.convert(addDto, UsrCoachBooking.class);
        this.save(usrcoachbooking);
    }

    @Override
    public void update(UsrCoachBookingUpdateDTO updateDto) {
        UsrCoachBooking usrcoachbooking = converter.convert(updateDto, UsrCoachBooking.class);
        this.updateById(usrcoachbooking);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrCoachBookingVO findById(Long id) {
        UsrCoachBooking entity = this.getById(id);
        return converter.convert(entity, UsrCoachBookingVO.class);
    }
}
