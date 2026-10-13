package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemPackageMapper;
import net.wanho.fitai.modules.member.model.dto.MemPackageAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemPackageQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemPackageUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemPackage;
import net.wanho.fitai.modules.member.model.vo.MemPackageVO;
import net.wanho.fitai.modules.member.service.MemPackageServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemPackageServiceImpl extends MPJBaseServiceImpl<MemPackageMapper, MemPackage> implements MemPackageServiceI {

    private final Converter converter;

    @Override
    public IPage<MemPackageVO> findByPage(MemPackageQueryDTO queryDto) {

        IPage<MemPackage> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPackageName()), MemPackage::getPackageName, queryDto.getPackageName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getLevelName()), MemPackage::getLevelName, queryDto.getLevelName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), MemPackage::getStatus, queryDto.getStatus());
        IPage<MemPackage> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemPackageVO.class));

    }

    @Override
    public void add(MemPackageAddDTO addDto) {
        MemPackage mempackage = converter.convert(addDto, MemPackage.class);
        this.save(mempackage);
    }

    @Override
    public void update(MemPackageUpdateDTO updateDto) {
        MemPackage mempackage = converter.convert(updateDto, MemPackage.class);
        this.updateById(mempackage);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemPackageVO findById(Long id) {
        MemPackage entity = this.getById(id);
        return converter.convert(entity, MemPackageVO.class);
    }
}
