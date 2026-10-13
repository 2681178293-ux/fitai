package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemberBenefitMapper;
import net.wanho.fitai.modules.member.model.dto.MemberBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemberBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemberBenefit;
import net.wanho.fitai.modules.member.model.vo.MemberBenefitVO;
import net.wanho.fitai.modules.member.service.MemberBenefitServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemberBenefitServiceImpl extends MPJBaseServiceImpl<MemberBenefitMapper, MemberBenefit> implements MemberBenefitServiceI {

    private final Converter converter;

    @Override
    public IPage<MemberBenefitVO> findByPage(MemberBenefitQueryDTO queryDto) {

        IPage<MemberBenefit> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemberBenefit> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCode()), MemberBenefit::getCode, queryDto.getCode());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getName()), MemberBenefit::getName, queryDto.getName());
        IPage<MemberBenefit> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemberBenefitVO.class));

    }

    @Override
    public void add(MemberBenefitAddDTO addDto) {
        MemberBenefit memberbenefit = converter.convert(addDto, MemberBenefit.class);
        this.save(memberbenefit);
    }

    @Override
    public void update(MemberBenefitUpdateDTO updateDto) {
        MemberBenefit memberbenefit = converter.convert(updateDto, MemberBenefit.class);
        this.updateById(memberbenefit);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemberBenefitVO findById(Long id) {
        MemberBenefit entity = this.getById(id);
        return converter.convert(entity, MemberBenefitVO.class);
    }
}
