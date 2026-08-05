package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysNoticeMapper;
import net.wanho.fitai.modules.system.model.dto.SysNoticeAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysNotice;
import net.wanho.fitai.modules.system.model.vo.SysNoticeVO;
import net.wanho.fitai.modules.system.service.SysNoticeServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends MPJBaseServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeServiceI {

    private final Converter converter;

    @Override
    public IPage<SysNoticeVO> findByPage(SysNoticeQueryDTO queryDto) {
        IPage<SysNotice> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getNoticeTitle()), SysNotice::getNoticeTitle, queryDto.getNoticeTitle());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getNoticeType()), SysNotice::getNoticeType, queryDto.getNoticeType());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysNotice::getStatus, queryDto.getStatus());
        IPage<SysNotice> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysNoticeVO.class));
    }

    @Override
    public void add(SysNoticeAddDTO addDto) {
        SysNotice entity = converter.convert(addDto, SysNotice.class);
        this.save(entity);
    }

    @Override
    public void update(SysNoticeUpdateDTO updateDto) {
        SysNotice entity = converter.convert(updateDto, SysNotice.class);
        this.updateById(entity);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysNoticeVO findById(Long id) {
        SysNotice entity = this.getById(id);
        return converter.convert(entity, SysNoticeVO.class);
    }
}
