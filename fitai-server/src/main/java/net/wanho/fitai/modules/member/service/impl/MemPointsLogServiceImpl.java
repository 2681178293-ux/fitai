package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemPointsLogMapper;
import net.wanho.fitai.modules.member.model.dto.MemPointsLogAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsLogQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsLogUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemPointsLog;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import net.wanho.fitai.modules.member.model.vo.MemPointsLogVO;
import net.wanho.fitai.modules.member.service.MemPointsLogServiceI;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemPointsLogServiceImpl extends MPJBaseServiceImpl<MemPointsLogMapper, MemPointsLog> implements MemPointsLogServiceI {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Converter converter;
    private final MemPointsLogMapper memPointsLogMapper;

    @Override
    public IPage<MemPointsLogVO> findByPage(MemPointsLogQueryDTO queryDto) {
        IPage<MemPointsLogVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        MPJLambdaWrapper<MemPointsLog> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(MemPointsLog.class);
        wrapper.selectAs(UsrCustomer::getNickName, MemPointsLogVO::getNickname);
        wrapper.selectAs(UsrCustomer::getRegisterSource, MemPointsLogVO::getRegisterSource);
        wrapper.leftJoin(UsrCustomer.class, UsrCustomer::getCustomerId, MemPointsLog::getCustomerId);

        wrapper.eq(queryDto.getCustomerId() != null, MemPointsLog::getCustomerId, queryDto.getCustomerId());
        wrapper.eq(StrUtil.isNotBlank(queryDto.getBizType()), MemPointsLog::getBizType, queryDto.getBizType());
        wrapper.eq(StrUtil.isNotBlank(queryDto.getRegisterSource()), UsrCustomer::getRegisterSource, queryDto.getRegisterSource());

        LocalDateTime startTime = parseDateTime(queryDto.getPointsStartTime());
        LocalDateTime endTime = parseDateTime(queryDto.getPointsEndTime());
        wrapper.ge(startTime != null, MemPointsLog::getCreateTime, startTime);
        wrapper.le(endTime != null, MemPointsLog::getCreateTime, endTime);
        wrapper.orderByDesc(MemPointsLog::getCreateTime);
        return memPointsLogMapper.selectJoinPage(page, MemPointsLogVO.class, wrapper);
    }

    private LocalDateTime parseDateTime(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return LocalDateTime.parse(value.trim(), DATE_TIME_FORMATTER);
    }

    @Override
    public void add(MemPointsLogAddDTO addDto) {
        MemPointsLog mempointslog = converter.convert(addDto, MemPointsLog.class);
        this.save(mempointslog);
    }

    @Override
    public void update(MemPointsLogUpdateDTO updateDto) {
        MemPointsLog mempointslog = converter.convert(updateDto, MemPointsLog.class);
        this.updateById(mempointslog);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemPointsLogVO findById(Long id) {
        MemPointsLog entity = this.getById(id);
        return converter.convert(entity, MemPointsLogVO.class);
    }
}
