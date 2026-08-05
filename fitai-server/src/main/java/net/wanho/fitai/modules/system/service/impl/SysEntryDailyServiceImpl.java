package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysEntryDailyMapper;
import net.wanho.fitai.modules.system.model.dto.SysEntryDailyAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysEntryDailyQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysEntryDailyUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysEntryDaily;
import net.wanho.fitai.modules.system.model.vo.SysEntryDailyVO;
import net.wanho.fitai.modules.system.service.SysEntryDailyServiceI;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SysEntryDailyServiceImpl extends MPJBaseServiceImpl<SysEntryDailyMapper, SysEntryDaily> implements SysEntryDailyServiceI {

    private final Converter converter;

    @Override
    public IPage<SysEntryDailyVO> findByPage(SysEntryDailyQueryDTO queryDto) {
        IPage<SysEntryDaily> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysEntryDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getChannel()), SysEntryDaily::getChannel, queryDto.getChannel());
        wrapper.eq(queryDto.getStatDate() != null, SysEntryDaily::getStatDate, queryDto.getStatDate());
        IPage<SysEntryDaily> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysEntryDailyVO.class));
    }

    @Override
    public void add(SysEntryDailyAddDTO addDto) {
        SysEntryDaily entity = converter.convert(addDto, SysEntryDaily.class);
        this.save(entity);
    }

    @Override
    public void update(SysEntryDailyUpdateDTO updateDto) {
        SysEntryDaily entity = converter.convert(updateDto, SysEntryDaily.class);
        this.update(entity, new LambdaQueryWrapper<SysEntryDaily>()
                .eq(SysEntryDaily::getStatDate, updateDto.getStatDate())
                .eq(SysEntryDaily::getChannel, updateDto.getChannel()));
    }

    @Override
    public void delete(LocalDate statDate, String channel) {
        this.remove(new LambdaQueryWrapper<SysEntryDaily>()
                .eq(SysEntryDaily::getStatDate, statDate)
                .eq(SysEntryDaily::getChannel, channel));
    }

    @Override
    public SysEntryDailyVO findById(LocalDate statDate, String channel) {
        SysEntryDaily entity = this.getOne(new LambdaQueryWrapper<SysEntryDaily>()
                .eq(SysEntryDaily::getStatDate, statDate)
                .eq(SysEntryDaily::getChannel, channel));
        return converter.convert(entity, SysEntryDailyVO.class);
    }
}
