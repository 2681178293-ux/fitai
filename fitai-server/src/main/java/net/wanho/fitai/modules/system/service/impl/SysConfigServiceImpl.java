package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.system.mapper.SysConfigMapper;
import net.wanho.fitai.modules.system.model.dto.SysConfigAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysConfigQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysConfigUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysConfig;
import net.wanho.fitai.modules.system.service.SysConfigServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends MPJBaseServiceImpl<SysConfigMapper, SysConfig> implements SysConfigServiceI {

    private final Converter converter;

    @Override
    public IPage<SysConfig> findByPage(SysConfigQueryDTO queryDto) {

        IPage<SysConfig> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getConfigName()), SysConfig::getConfigName, queryDto.getConfigName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getConfigKey()), SysConfig::getConfigKey, queryDto.getConfigKey());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysConfig::getStatus, queryDto.getStatus());
        return this.page(page, wrapper);

    }

    @Override
    public void add(SysConfigAddDTO addDto) {
        SysConfig sysConfig = converter.convert(addDto, SysConfig.class);
        this.save(sysConfig);
    }

    @Override
    public void update(SysConfigUpdateDTO updateDto) {
        SysConfig sysConfig = converter.convert(updateDto, SysConfig.class);
        this.updateById(sysConfig);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }
}
