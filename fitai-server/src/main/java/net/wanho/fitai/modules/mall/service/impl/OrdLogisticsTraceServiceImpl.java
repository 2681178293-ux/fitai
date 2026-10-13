package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.OrdLogisticsTraceMapper;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsTraceAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsTraceQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsTraceUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdLogisticsTrace;
import net.wanho.fitai.modules.mall.model.vo.OrdLogisticsTraceVO;
import net.wanho.fitai.modules.mall.service.OrdLogisticsTraceServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OrdLogisticsTraceServiceImpl extends MPJBaseServiceImpl<OrdLogisticsTraceMapper, OrdLogisticsTrace> implements OrdLogisticsTraceServiceI {

    private final Converter converter;

    @Override
    public IPage<OrdLogisticsTraceVO> findByPage(OrdLogisticsTraceQueryDTO queryDto) {

        IPage<OrdLogisticsTrace> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<OrdLogisticsTrace> wrapper = new LambdaQueryWrapper<>();
        IPage<OrdLogisticsTrace> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, OrdLogisticsTraceVO.class));

    }

    @Override
    public void add(OrdLogisticsTraceAddDTO addDto) {
        OrdLogisticsTrace ordlogisticstrace = converter.convert(addDto, OrdLogisticsTrace.class);
        this.save(ordlogisticstrace);
    }

    @Override
    public void update(OrdLogisticsTraceUpdateDTO updateDto) {
        OrdLogisticsTrace ordlogisticstrace = converter.convert(updateDto, OrdLogisticsTrace.class);
        this.updateById(ordlogisticstrace);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public OrdLogisticsTraceVO findById(Long id) {
        OrdLogisticsTrace entity = this.getById(id);
        return converter.convert(entity, OrdLogisticsTraceVO.class);
    }
}
