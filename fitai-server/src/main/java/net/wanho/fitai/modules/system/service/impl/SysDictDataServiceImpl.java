package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysDictDataMapper;
import net.wanho.fitai.modules.system.model.dto.SysDictDataAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictDataQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictDataUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysDictData;
import net.wanho.fitai.modules.system.model.vo.SysDictDataVO;
import net.wanho.fitai.modules.system.service.SysDictDataServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends MPJBaseServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataServiceI {

    private final Converter converter;

    @Override
    public IPage<SysDictDataVO> findByPage(SysDictDataQueryDTO queryDto) {
        IPage<SysDictData> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getDictLabel()), SysDictData::getDictLabel, queryDto.getDictLabel());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getDictType()), SysDictData::getDictType, queryDto.getDictType());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysDictData::getStatus, queryDto.getStatus());
        IPage<SysDictData> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysDictDataVO.class));
    }

    @Override
    public void add(SysDictDataAddDTO addDto) {
        SysDictData entity = converter.convert(addDto, SysDictData.class);
        this.save(entity);
    }

    @Override
    public void update(SysDictDataUpdateDTO updateDto) {
        SysDictData entity = converter.convert(updateDto, SysDictData.class);
        this.updateById(entity);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysDictDataVO findById(Long id) {
        SysDictData entity = this.getById(id);
        return converter.convert(entity, SysDictDataVO.class);
    }

    @Override
    public List<SysDictDataVO> findByDictType(String dictType) {
        List<SysDictData> dictDataList = this.list(new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, dictType));
        return dictDataList.stream().map(item -> converter.convert(item, SysDictDataVO.class)).collect(Collectors.toList());
    }
}
