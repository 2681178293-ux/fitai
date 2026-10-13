package net.wanho.fitai.modules.coach.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.coach.mapper.UsrCoachScheduleMapper;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachScheduleAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachScheduleQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachScheduleUpdateDTO;
import net.wanho.fitai.modules.coach.model.po.UsrCoachSchedule;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachScheduleVO;
import net.wanho.fitai.modules.coach.service.UsrCoachScheduleServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsrCoachScheduleServiceImpl extends MPJBaseServiceImpl<UsrCoachScheduleMapper, UsrCoachSchedule> implements UsrCoachScheduleServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrCoachScheduleVO> findByPage(UsrCoachScheduleQueryDTO queryDto) {

        IPage<UsrCoachSchedule> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<UsrCoachSchedule> wrapper = new LambdaQueryWrapper<>();
        IPage<UsrCoachSchedule> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, UsrCoachScheduleVO.class));

    }

    @Override
    public void add(UsrCoachScheduleAddDTO addDto) {
        UsrCoachSchedule usrcoachschedule = converter.convert(addDto, UsrCoachSchedule.class);
        this.save(usrcoachschedule);
    }

    @Override
    public void update(UsrCoachScheduleUpdateDTO updateDto) {
        UsrCoachSchedule usrcoachschedule = converter.convert(updateDto, UsrCoachSchedule.class);
        this.updateById(usrcoachschedule);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrCoachScheduleVO findById(Long id) {
        UsrCoachSchedule entity = this.getById(id);
        return converter.convert(entity, UsrCoachScheduleVO.class);
    }
}
