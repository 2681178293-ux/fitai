package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.OrdLogisticsMapper;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdLogistics;
import net.wanho.fitai.modules.mall.model.vo.OrdLogisticsVO;
import net.wanho.fitai.modules.mall.service.OrdLogisticsServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrdLogisticsServiceImpl extends MPJBaseServiceImpl<OrdLogisticsMapper, OrdLogistics> implements OrdLogisticsServiceI {

    private final Converter converter;

    @Override
    public IPage<OrdLogisticsVO> findByPage(OrdLogisticsQueryDTO queryDto) {

        IPage<OrdLogistics> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<OrdLogistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getOrderNo()), OrdLogistics::getOrderNo, queryDto.getOrderNo());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getExpressCode()), OrdLogistics::getExpressCode, queryDto.getExpressCode());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getExpressName()), OrdLogistics::getExpressName, queryDto.getExpressName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getTrackingNo()), OrdLogistics::getTrackingNo, queryDto.getTrackingNo());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), OrdLogistics::getStatus, queryDto.getStatus());
        IPage<OrdLogistics> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, OrdLogisticsVO.class));

    }

    @Override
    public void add(OrdLogisticsAddDTO addDto) {
        OrdLogistics ordlogistics = converter.convert(addDto, OrdLogistics.class);
        this.save(ordlogistics);
    }

    @Override
    public void update(OrdLogisticsUpdateDTO updateDto) {
        OrdLogistics ordlogistics = converter.convert(updateDto, OrdLogistics.class);
        this.updateById(ordlogistics);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public OrdLogisticsVO findById(Long id) {
        OrdLogistics entity = this.getById(id);
        return converter.convert(entity, OrdLogisticsVO.class);
    }
}
