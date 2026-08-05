package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysJobMapper;
import net.wanho.fitai.modules.system.model.dto.SysJobAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysJobQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysJobUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysJob;
import net.wanho.fitai.modules.system.model.vo.SysJobVO;
import net.wanho.fitai.modules.system.service.SysJobServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SysJobServiceImpl extends MPJBaseServiceImpl<SysJobMapper, SysJob> implements SysJobServiceI {

    private final Converter converter;

    @Override
    public IPage<SysJobVO> findByPage(SysJobQueryDTO queryDto) {
        IPage<SysJob> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getJobName()), SysJob::getJobName, queryDto.getJobName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getJobGroup()), SysJob::getJobGroup, queryDto.getJobGroup());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysJob::getStatus, queryDto.getStatus());
        IPage<SysJob> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysJobVO.class));
    }

    @Override
    public void add(SysJobAddDTO addDto) {
        SysJob entity = converter.convert(addDto, SysJob.class);
        this.save(entity);
    }

    @Override
    public void update(SysJobUpdateDTO updateDto) {
        SysJob entity = converter.convert(updateDto, SysJob.class);
        this.updateById(entity);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysJobVO findById(Long id) {
        SysJob entity = this.getById(id);
        return converter.convert(entity, SysJobVO.class);
    }
}
