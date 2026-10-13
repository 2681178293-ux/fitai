package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemberOrderMapper;
import net.wanho.fitai.modules.member.model.dto.MemberOrderAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberOrderQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemberOrderUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemberOrder;
import net.wanho.fitai.modules.member.model.vo.MemberOrderVO;
import net.wanho.fitai.modules.member.service.MemberOrderServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemberOrderServiceImpl extends MPJBaseServiceImpl<MemberOrderMapper, MemberOrder> implements MemberOrderServiceI {

    private final Converter converter;

    @Override
    public IPage<MemberOrderVO> findByPage(MemberOrderQueryDTO queryDto) {

        IPage<MemberOrder> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemberOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOrderNo()), MemberOrder::getOrderNo, queryDto.getOrderNo());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPlanName()), MemberOrder::getPlanName, queryDto.getPlanName());
        IPage<MemberOrder> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemberOrderVO.class));

    }

    @Override
    public void add(MemberOrderAddDTO addDto) {
        MemberOrder memberorder = converter.convert(addDto, MemberOrder.class);
        this.save(memberorder);
    }

    @Override
    public void update(MemberOrderUpdateDTO updateDto) {
        MemberOrder memberorder = converter.convert(updateDto, MemberOrder.class);
        this.updateById(memberorder);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemberOrderVO findById(Long id) {
        MemberOrder entity = this.getById(id);
        return converter.convert(entity, MemberOrderVO.class);
    }
}
