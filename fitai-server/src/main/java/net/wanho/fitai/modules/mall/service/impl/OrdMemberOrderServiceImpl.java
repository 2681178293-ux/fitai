package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.OrdMemberOrderMapper;
import net.wanho.fitai.modules.mall.model.dto.OrdMemberOrderAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdMemberOrderQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdMemberOrderUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdMemberOrder;
import net.wanho.fitai.modules.mall.model.vo.OrdMemberOrderVO;
import net.wanho.fitai.modules.mall.service.OrdMemberOrderServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrdMemberOrderServiceImpl extends MPJBaseServiceImpl<OrdMemberOrderMapper, OrdMemberOrder> implements OrdMemberOrderServiceI {

    private final Converter converter;

    @Override
    public IPage<OrdMemberOrderVO> findByPage(OrdMemberOrderQueryDTO queryDto) {

        IPage<OrdMemberOrder> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<OrdMemberOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOrderNo()), OrdMemberOrder::getOrderNo, queryDto.getOrderNo());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPackageName()), OrdMemberOrder::getPackageName, queryDto.getPackageName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), OrdMemberOrder::getStatus, queryDto.getStatus());
        IPage<OrdMemberOrder> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, OrdMemberOrderVO.class));

    }

    @Override
    public void add(OrdMemberOrderAddDTO addDto) {
        OrdMemberOrder ordmemberorder = converter.convert(addDto, OrdMemberOrder.class);
        this.save(ordmemberorder);
    }

    @Override
    public void update(OrdMemberOrderUpdateDTO updateDto) {
        OrdMemberOrder ordmemberorder = converter.convert(updateDto, OrdMemberOrder.class);
        this.updateById(ordmemberorder);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public OrdMemberOrderVO findById(Long id) {
        OrdMemberOrder entity = this.getById(id);
        return converter.convert(entity, OrdMemberOrderVO.class);
    }
}
