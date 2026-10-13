package net.wanho.fitai.modules.coach.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.coach.mapper.UsrCoachBindMapper;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBindAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBindQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBindUpdateDTO;
import net.wanho.fitai.modules.coach.model.po.UsrCoachBind;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachBindVO;
import net.wanho.fitai.modules.coach.service.UsrCoachBindServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsrCoachBindServiceImpl extends MPJBaseServiceImpl<UsrCoachBindMapper, UsrCoachBind> implements UsrCoachBindServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrCoachBindVO> findByPage(UsrCoachBindQueryDTO queryDto) {
        IPage<UsrCoachBind> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<UsrCoachBind> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDto.getCoachName()), UsrCoachBind::getCoachName, queryDto.getCoachName());
        wrapper.eq(queryDto.getCustomerId() != null, UsrCoachBind::getCustomerId, queryDto.getCustomerId());
        wrapper.eq(StrUtil.isNotBlank(queryDto.getBindType()), UsrCoachBind::getBindType, queryDto.getBindType());
        wrapper.eq(StrUtil.isNotBlank(queryDto.getStatus()), UsrCoachBind::getStatus, queryDto.getStatus());
        wrapper.orderByDesc(UsrCoachBind::getCreateTime);
        return this.page(page, wrapper).convert(item -> converter.convert(item, UsrCoachBindVO.class));
    }

    @Override
    public void add(UsrCoachBindAddDTO addDto) {
        UsrCoachBind usrcoachbind = converter.convert(addDto, UsrCoachBind.class);
        this.save(usrcoachbind);
    }

    @Override
    public void update(UsrCoachBindUpdateDTO updateDto) {
        UsrCoachBind usrcoachbind = converter.convert(updateDto, UsrCoachBind.class);
        this.updateById(usrcoachbind);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrCoachBindVO findById(Long id) {
        UsrCoachBind entity = this.getById(id);
        return converter.convert(entity, UsrCoachBindVO.class);
    }
}
