package net.wanho.fitai.modules.coach.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.coach.mapper.UsrCoachMapper;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachUpdateDTO;
import net.wanho.fitai.modules.coach.model.po.UsrCoach;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachVO;
import net.wanho.fitai.modules.coach.service.UsrCoachServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsrCoachServiceImpl extends MPJBaseServiceImpl<UsrCoachMapper, UsrCoach> implements UsrCoachServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrCoachVO> findByPage(UsrCoachQueryDTO queryDto) {

        IPage<UsrCoach> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<UsrCoach> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StrUtil.isNotEmpty(queryDto.getCoachName()), UsrCoach::getCoachName, queryDto.getCoachName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), UsrCoach::getStatus, queryDto.getStatus());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSpecialty()), UsrCoach::getSpecialty, queryDto.getSpecialty());
        IPage<UsrCoach> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, UsrCoachVO.class));

    }

    @Override
    public void add(UsrCoachAddDTO addDto) {
        UsrCoach usrcoach = converter.convert(addDto, UsrCoach.class);
        this.save(usrcoach);
    }

    @Override
    public void update(UsrCoachUpdateDTO updateDto) {
        UsrCoach usrcoach = converter.convert(updateDto, UsrCoach.class);
        this.updateById(usrcoach);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrCoachVO findById(Long id) {
        UsrCoach entity = this.getById(id);
        return converter.convert(entity, UsrCoachVO.class);
    }
}
