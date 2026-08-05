package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysJobLogMapper;
import net.wanho.fitai.modules.system.model.dto.SysJobLogQueryDTO;
import net.wanho.fitai.modules.system.model.po.SysJobLog;
import net.wanho.fitai.modules.system.model.vo.SysJobLogVO;
import net.wanho.fitai.modules.system.service.SysJobLogServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SysJobLogServiceImpl extends MPJBaseServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogServiceI {

    private final Converter converter;

    @Override
    public IPage<SysJobLogVO> findByPage(SysJobLogQueryDTO queryDto) {
        IPage<SysJobLog> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysJobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getJobName()), SysJobLog::getJobName, queryDto.getJobName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getJobGroup()), SysJobLog::getJobGroup, queryDto.getJobGroup());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysJobLog::getStatus, queryDto.getStatus());
        IPage<SysJobLog> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysJobLogVO.class));
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysJobLogVO findById(Long id) {
        SysJobLog entity = this.getById(id);
        return converter.convert(entity, SysJobLogVO.class);
    }
}
