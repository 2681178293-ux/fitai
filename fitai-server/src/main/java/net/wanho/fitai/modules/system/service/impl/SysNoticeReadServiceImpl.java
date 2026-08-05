package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysNoticeReadMapper;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysNoticeRead;
import net.wanho.fitai.modules.system.model.vo.SysNoticeReadVO;
import net.wanho.fitai.modules.system.service.SysNoticeReadServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SysNoticeReadServiceImpl extends MPJBaseServiceImpl<SysNoticeReadMapper, SysNoticeRead> implements SysNoticeReadServiceI {

    private final Converter converter;

    @Override
    public IPage<SysNoticeReadVO> findByPage(SysNoticeReadQueryDTO queryDto) {
        IPage<SysNoticeRead> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysNoticeRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDto.getNoticeId() != null, SysNoticeRead::getNoticeId, queryDto.getNoticeId());
        wrapper.eq(queryDto.getCustomerId() != null, SysNoticeRead::getCustomerId, queryDto.getCustomerId());
        IPage<SysNoticeRead> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysNoticeReadVO.class));
    }

    @Override
    public void add(SysNoticeReadAddDTO addDto) {
        SysNoticeRead entity = converter.convert(addDto, SysNoticeRead.class);
        this.save(entity);
    }

    @Override
    public void update(SysNoticeReadUpdateDTO updateDto) {
        SysNoticeRead entity = converter.convert(updateDto, SysNoticeRead.class);
        this.updateById(entity);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysNoticeReadVO findById(Long id) {
        SysNoticeRead entity = this.getById(id);
        return converter.convert(entity, SysNoticeReadVO.class);
    }
}
