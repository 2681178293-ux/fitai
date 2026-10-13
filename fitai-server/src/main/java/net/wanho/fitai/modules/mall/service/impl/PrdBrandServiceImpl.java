package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdBrandMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdBrandAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdBrandQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdBrandUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdBrand;
import net.wanho.fitai.modules.mall.model.vo.PrdBrandVO;
import net.wanho.fitai.modules.mall.service.PrdBrandServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrdBrandServiceImpl extends MPJBaseServiceImpl<PrdBrandMapper, PrdBrand> implements PrdBrandServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdBrandVO> findByPage(PrdBrandQueryDTO queryDto) {

        IPage<PrdBrand> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getBrandName()), PrdBrand::getBrandName, queryDto.getBrandName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), PrdBrand::getStatus, queryDto.getStatus());
        IPage<PrdBrand> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdBrandVO.class));

    }

    // 获取品牌下拉选择框数据
    @Override
    public List<PrdBrandVO> getBrandSelect() {
        return this.list().stream().map(item -> converter.convert(item, PrdBrandVO.class)).collect(Collectors.toList());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PrdBrandAddDTO addDto) {
        PrdBrand prdbrand = converter.convert(addDto, PrdBrand.class);
        this.save(prdbrand);
    }

    @Override
    public void update(PrdBrandUpdateDTO updateDto) {
        PrdBrand prdbrand = converter.convert(updateDto, PrdBrand.class);
        this.updateById(prdbrand);

    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdBrandVO findById(Long id) {
        PrdBrand entity = this.getById(id);
        return converter.convert(entity, PrdBrandVO.class);
    }


}
