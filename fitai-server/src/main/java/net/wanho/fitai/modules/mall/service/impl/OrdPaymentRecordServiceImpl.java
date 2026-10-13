package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.OrdPaymentRecordMapper;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentRecordAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentRecordQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentRecordUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdPaymentRecord;
import net.wanho.fitai.modules.mall.model.vo.OrdPaymentRecordVO;
import net.wanho.fitai.modules.mall.service.OrdPaymentRecordServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrdPaymentRecordServiceImpl extends MPJBaseServiceImpl<OrdPaymentRecordMapper, OrdPaymentRecord> implements OrdPaymentRecordServiceI {

    private final Converter converter;

    @Override
    public IPage<OrdPaymentRecordVO> findByPage(OrdPaymentRecordQueryDTO queryDto) {

        IPage<OrdPaymentRecord> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<OrdPaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPaymentNo()), OrdPaymentRecord::getPaymentNo, queryDto.getPaymentNo());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getMergeNo()), OrdPaymentRecord::getMergeNo, queryDto.getMergeNo());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOrderNo()), OrdPaymentRecord::getOrderNo, queryDto.getOrderNo());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getChannelCode()), OrdPaymentRecord::getChannelCode, queryDto.getChannelCode());
        IPage<OrdPaymentRecord> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, OrdPaymentRecordVO.class));

    }

    @Override
    public void add(OrdPaymentRecordAddDTO addDto) {
        OrdPaymentRecord ordpaymentrecord = converter.convert(addDto, OrdPaymentRecord.class);
        this.save(ordpaymentrecord);
    }

    @Override
    public void update(OrdPaymentRecordUpdateDTO updateDto) {
        OrdPaymentRecord ordpaymentrecord = converter.convert(updateDto, OrdPaymentRecord.class);
        this.updateById(ordpaymentrecord);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public OrdPaymentRecordVO findById(Long id) {
        OrdPaymentRecord entity = this.getById(id);
        return converter.convert(entity, OrdPaymentRecordVO.class);
    }
}
