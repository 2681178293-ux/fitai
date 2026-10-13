package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemBenefitMapper;
import net.wanho.fitai.modules.member.model.dto.MemBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemBenefit;
import net.wanho.fitai.modules.member.model.vo.MemBenefitVO;
import net.wanho.fitai.modules.member.service.MemBenefitServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemBenefitServiceImpl extends MPJBaseServiceImpl<MemBenefitMapper, MemBenefit> implements MemBenefitServiceI {

    private final Converter converter;

    @Override
    public IPage<MemBenefitVO> findByPage(MemBenefitQueryDTO queryDto) {

        IPage<MemBenefit> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemBenefit> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getBenefitName()), MemBenefit::getBenefitName, queryDto.getBenefitName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getBenefitCode()), MemBenefit::getBenefitCode, queryDto.getBenefitCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), MemBenefit::getStatus, queryDto.getStatus());
        IPage<MemBenefit> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemBenefitVO.class));

    }

    @Override
    public void add(MemBenefitAddDTO addDto) {
        MemBenefit membenefit = converter.convert(addDto, MemBenefit.class);
        this.save(membenefit);
    }

    @Override
    public void update(MemBenefitUpdateDTO updateDto) {
        MemBenefit membenefit = converter.convert(updateDto, MemBenefit.class);
        this.updateById(membenefit);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemBenefitVO findById(Long id) {
        MemBenefit entity = this.getById(id);
        return converter.convert(entity, MemBenefitVO.class);
    }
}
