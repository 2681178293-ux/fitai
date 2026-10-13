package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdBrandMapper;
import net.wanho.fitai.modules.mall.mapper.PrdSpuMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdBrand;
import net.wanho.fitai.modules.mall.model.po.PrdSpu;
import net.wanho.fitai.modules.mall.model.vo.PrdSpuVO;
import net.wanho.fitai.modules.mall.service.PrdSpuServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrdSpuServiceImpl extends MPJBaseServiceImpl<PrdSpuMapper, PrdSpu> implements PrdSpuServiceI {

    private final Converter converter;
    private final PrdBrandMapper prdBrandMapper;

    @Override
    public IPage<PrdSpuVO> findByPage(PrdSpuQueryDTO queryDto) {

        IPage<PrdSpu> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdSpu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSpuName()), PrdSpu::getSpuName, queryDto.getSpuName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSubtitle()), PrdSpu::getSubtitle, queryDto.getSubtitle());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), PrdSpu::getStatus, queryDto.getStatus());

        if (StrUtil.isNotEmpty(queryDto.getBrandName())) {
            List<Long> brandIds = prdBrandMapper.selectList(
                            new LambdaQueryWrapper<PrdBrand>()
                                    .like(PrdBrand::getBrandName, queryDto.getBrandName())
                                    .select(PrdBrand::getBrandId)
                    ).stream()
                    .map(PrdBrand::getBrandId)
                    .toList();
            if (brandIds.isEmpty()) {
                page.setRecords(Collections.emptyList());
                page.setTotal(0);
                return page.convert(item -> converter.convert(item, PrdSpuVO.class));
            }
            wrapper.in(PrdSpu::getBrandId, brandIds);
        }

        IPage<PrdSpu> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdSpuVO.class));

    }

    @Override
    public void add(PrdSpuAddDTO addDto) {
        PrdSpu prdspu = converter.convert(addDto, PrdSpu.class);
        this.save(prdspu);
    }

    @Override
    public void update(PrdSpuUpdateDTO updateDto) {
        PrdSpu prdspu = converter.convert(updateDto, PrdSpu.class);
        this.updateById(prdspu);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdSpuVO findById(Long id) {
        PrdSpu entity = this.getById(id);
        return converter.convert(entity, PrdSpuVO.class);
    }

    @Override
    public void updateProductStatus(Long spuId, String status) {
        PrdSpu prdSpu = new PrdSpu();
        prdSpu.setSpuId(spuId);
        prdSpu.setStatus(status);
        this.updateById(prdSpu);
    }
}
