package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysDeptMapper;
import net.wanho.fitai.modules.system.model.dto.SysDeptAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDeptQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDeptUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysDept;
import net.wanho.fitai.modules.system.model.vo.SysDeptVO;
import net.wanho.fitai.modules.system.service.SysDeptServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends MPJBaseServiceImpl<SysDeptMapper, SysDept> implements SysDeptServiceI {

    private final Converter converter;

    @Override
    public IPage<SysDeptVO> findByPage(SysDeptQueryDTO queryDto) {
        IPage<SysDept> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getDeptName()), SysDept::getDeptName, queryDto.getDeptName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysDept::getStatus, queryDto.getStatus());
        IPage<SysDept> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysDeptVO.class));
    }

    @Override
    public void add(SysDeptAddDTO addDto) {
        SysDept entity = converter.convert(addDto, SysDept.class);
        this.save(entity);
    }

    @Override
    public void update(SysDeptUpdateDTO updateDto) {
        SysDept entity = converter.convert(updateDto, SysDept.class);
        this.updateById(entity);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysDeptVO findById(Long id) {
        SysDept entity = this.getById(id);
        return converter.convert(entity, SysDeptVO.class);
    }
}
