package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.MktBannerMapper;
import net.wanho.fitai.modules.marketing.model.dto.MktBannerAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktBannerQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktBannerUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktBanner;
import net.wanho.fitai.modules.marketing.model.vo.MktBannerVO;
import net.wanho.fitai.modules.marketing.service.MktBannerServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MktBannerServiceImpl extends MPJBaseServiceImpl<MktBannerMapper, MktBanner> implements MktBannerServiceI {

    private final Converter converter;

    @Override
    public IPage<MktBannerVO> findByPage(MktBannerQueryDTO queryDto) {

        IPage<MktBanner> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MktBanner> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getTitle()), MktBanner::getTitle, queryDto.getTitle());
        IPage<MktBanner> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MktBannerVO.class));

    }

    @Override
    public void add(MktBannerAddDTO addDto) {
        MktBanner mktbanner = converter.convert(addDto, MktBanner.class);
        this.save(mktbanner);
    }

    @Override
    public void update(MktBannerUpdateDTO updateDto) {
        MktBanner mktbanner = converter.convert(updateDto, MktBanner.class);
        this.updateById(mktbanner);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MktBannerVO findById(Long id) {
        MktBanner entity = this.getById(id);
        return converter.convert(entity, MktBannerVO.class);
    }
}
