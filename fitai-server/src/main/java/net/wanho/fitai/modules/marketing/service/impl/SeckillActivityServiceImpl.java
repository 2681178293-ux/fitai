package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.SeckillActivityMapper;
import net.wanho.fitai.modules.marketing.model.dto.SeckillActivityAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillActivityQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillActivityUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.SeckillActivity;
import net.wanho.fitai.modules.marketing.model.vo.SeckillActivityVO;
import net.wanho.fitai.modules.marketing.service.SeckillActivityServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SeckillActivityServiceImpl extends MPJBaseServiceImpl<SeckillActivityMapper, SeckillActivity> implements SeckillActivityServiceI {

    private final Converter converter;

    @Override
    public IPage<SeckillActivityVO> findByPage(SeckillActivityQueryDTO queryDto) {

        IPage<SeckillActivity> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SeckillActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getName()), SeckillActivity::getName, queryDto.getName());
        IPage<SeckillActivity> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SeckillActivityVO.class));

    }

    @Override
    public void add(SeckillActivityAddDTO addDto) {
        SeckillActivity seckillactivity = converter.convert(addDto, SeckillActivity.class);
        this.save(seckillactivity);
    }

    @Override
    public void update(SeckillActivityUpdateDTO updateDto) {
        SeckillActivity seckillactivity = converter.convert(updateDto, SeckillActivity.class);
        this.updateById(seckillactivity);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SeckillActivityVO findById(Long id) {
        SeckillActivity entity = this.getById(id);
        return converter.convert(entity, SeckillActivityVO.class);
    }
}
