package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysDictTypeMapper;
import net.wanho.fitai.modules.system.model.dto.SysDictTypeAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictTypeQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictTypeUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysDictType;
import net.wanho.fitai.modules.system.model.vo.SysDictTypeVO;
import net.wanho.fitai.modules.system.service.SysDictTypeServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends MPJBaseServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeServiceI {

    private final Converter converter;

    @Override
    public IPage<SysDictTypeVO> findByPage(SysDictTypeQueryDTO queryDto) {
        IPage<SysDictType> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getDictName()), SysDictType::getDictName, queryDto.getDictName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getDictType()), SysDictType::getDictType, queryDto.getDictType());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysDictType::getStatus, queryDto.getStatus());
        IPage<SysDictType> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysDictTypeVO.class));
    }

    @Override
    public void add(SysDictTypeAddDTO addDto) {
        SysDictType entity = converter.convert(addDto, SysDictType.class);
        this.save(entity);
    }

    @Override
    public void update(SysDictTypeUpdateDTO updateDto) {
        SysDictType entity = converter.convert(updateDto, SysDictType.class);
        this.updateById(entity);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysDictTypeVO findById(Long id) {
        SysDictType entity = this.getById(id);
        return converter.convert(entity, SysDictTypeVO.class);
    }
}
