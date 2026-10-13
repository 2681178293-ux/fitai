package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.OrdRefundMapper;
import net.wanho.fitai.modules.mall.model.dto.OrdRefundAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdRefundQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdRefundUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdRefund;
import net.wanho.fitai.modules.mall.model.vo.OrdRefundVO;
import net.wanho.fitai.modules.mall.service.OrdRefundServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrdRefundServiceImpl extends MPJBaseServiceImpl<OrdRefundMapper, OrdRefund> implements OrdRefundServiceI {

    private final Converter converter;

    @Override
    public IPage<OrdRefundVO> findByPage(OrdRefundQueryDTO queryDto) {

        IPage<OrdRefund> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<OrdRefund> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRefundNo()), OrdRefund::getRefundNo, queryDto.getRefundNo());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOrderNo()), OrdRefund::getOrderNo, queryDto.getOrderNo());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), OrdRefund::getStatus, queryDto.getStatus());
        IPage<OrdRefund> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, OrdRefundVO.class));

    }

    @Override
    public void add(OrdRefundAddDTO addDto) {
        OrdRefund ordrefund = converter.convert(addDto, OrdRefund.class);
        this.save(ordrefund);
    }

    @Override
    public void update(OrdRefundUpdateDTO updateDto) {
        OrdRefund ordrefund = converter.convert(updateDto, OrdRefund.class);
        this.updateById(ordrefund);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public OrdRefundVO findById(Long id) {
        OrdRefund entity = this.getById(id);
        return converter.convert(entity, OrdRefundVO.class);
    }
}
