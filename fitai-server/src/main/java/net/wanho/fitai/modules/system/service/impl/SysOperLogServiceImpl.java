package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysOperLogMapper;
import net.wanho.fitai.modules.system.model.dto.SysOperLogQueryDTO;
import net.wanho.fitai.modules.system.model.po.SysOperLog;
import net.wanho.fitai.modules.system.model.vo.SysOperLogVO;
import net.wanho.fitai.modules.system.service.SysOperLogServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl extends MPJBaseServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogServiceI {

    private final Converter converter;

    @Override
    public IPage<SysOperLogVO> findByPage(SysOperLogQueryDTO queryDto) {
        IPage<SysOperLog> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getTitle()), SysOperLog::getTitle, queryDto.getTitle());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOperName()), SysOperLog::getOperName, queryDto.getOperName());
        wrapper.eq(queryDto.getBusinessType() != null, SysOperLog::getBusinessType, queryDto.getBusinessType());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysOperLog::getStatus, queryDto.getStatus());
        IPage<SysOperLog> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysOperLogVO.class));
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysOperLogVO findById(Long id) {
        SysOperLog entity = this.getById(id);
        return converter.convert(entity, SysOperLogVO.class);
    }
}
