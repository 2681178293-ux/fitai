package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemCustomerBadgeMapper;
import net.wanho.fitai.modules.member.model.dto.MemCustomerBadgeAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemCustomerBadgeQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemCustomerBadgeUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemCustomerBadge;
import net.wanho.fitai.modules.member.model.vo.MemCustomerBadgeVO;
import net.wanho.fitai.modules.member.service.MemCustomerBadgeServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemCustomerBadgeServiceImpl extends MPJBaseServiceImpl<MemCustomerBadgeMapper, MemCustomerBadge> implements MemCustomerBadgeServiceI {

    private final Converter converter;

    @Override
    public IPage<MemCustomerBadgeVO> findByPage(MemCustomerBadgeQueryDTO queryDto) {

        IPage<MemCustomerBadge> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemCustomerBadge> wrapper = new LambdaQueryWrapper<>();
        IPage<MemCustomerBadge> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemCustomerBadgeVO.class));

    }

    @Override
    public void add(MemCustomerBadgeAddDTO addDto) {
        MemCustomerBadge memcustomerbadge = converter.convert(addDto, MemCustomerBadge.class);
        this.save(memcustomerbadge);
    }

    @Override
    public void update(MemCustomerBadgeUpdateDTO updateDto) {
        MemCustomerBadge memcustomerbadge = converter.convert(updateDto, MemCustomerBadge.class);
        this.updateById(memcustomerbadge);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemCustomerBadgeVO findById(Long id) {
        MemCustomerBadge entity = this.getById(id);
        return converter.convert(entity, MemCustomerBadgeVO.class);
    }
}
