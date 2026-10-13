package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.SeckillSkuMapper;
import net.wanho.fitai.modules.marketing.model.dto.SeckillSkuAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillSkuQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillSkuUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.SeckillSku;
import net.wanho.fitai.modules.marketing.model.vo.SeckillSkuVO;
import net.wanho.fitai.modules.marketing.service.SeckillSkuServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SeckillSkuServiceImpl extends MPJBaseServiceImpl<SeckillSkuMapper, SeckillSku> implements SeckillSkuServiceI {

    private final Converter converter;

    @Override
    public IPage<SeckillSkuVO> findByPage(SeckillSkuQueryDTO queryDto) {

        IPage<SeckillSku> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SeckillSku> wrapper = new LambdaQueryWrapper<>();
        IPage<SeckillSku> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SeckillSkuVO.class));

    }

    @Override
    public void add(SeckillSkuAddDTO addDto) {
        SeckillSku seckillsku = converter.convert(addDto, SeckillSku.class);
        this.save(seckillsku);
    }

    @Override
    public void update(SeckillSkuUpdateDTO updateDto) {
        SeckillSku seckillsku = converter.convert(updateDto, SeckillSku.class);
        this.updateById(seckillsku);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SeckillSkuVO findById(Long id) {
        SeckillSku entity = this.getById(id);
        return converter.convert(entity, SeckillSkuVO.class);
    }
}
