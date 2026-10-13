package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemBadgeMapper;
import net.wanho.fitai.modules.member.model.dto.MemBadgeAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemBadgeQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemBadgeUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemBadge;
import net.wanho.fitai.modules.member.model.vo.MemBadgeVO;
import net.wanho.fitai.modules.member.service.MemBadgeServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemBadgeServiceImpl extends MPJBaseServiceImpl<MemBadgeMapper, MemBadge> implements MemBadgeServiceI {

    private final Converter converter;

    @Override
    public IPage<MemBadgeVO> findByPage(MemBadgeQueryDTO queryDto) {

        IPage<MemBadge> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemBadge> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getBadgeCode()), MemBadge::getBadgeCode, queryDto.getBadgeCode());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getBadgeName()), MemBadge::getBadgeName, queryDto.getBadgeName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCategory()), MemBadge::getCategory, queryDto.getCategory());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), MemBadge::getStatus, queryDto.getStatus());
        IPage<MemBadge> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemBadgeVO.class));

    }

    @Override
    public void add(MemBadgeAddDTO addDto) {
        MemBadge membadge = converter.convert(addDto, MemBadge.class);
        this.save(membadge);
    }

    @Override
    public void update(MemBadgeUpdateDTO updateDto) {
        MemBadge membadge = converter.convert(updateDto, MemBadge.class);
        this.updateById(membadge);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemBadgeVO findById(Long id) {
        MemBadge entity = this.getById(id);
        return converter.convert(entity, MemBadgeVO.class);
    }
}
