package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemberLevelBenefitMapper;
import net.wanho.fitai.modules.member.model.dto.MemberLevelBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberLevelBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemberLevelBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemberLevelBenefit;
import net.wanho.fitai.modules.member.model.vo.MemberLevelBenefitVO;
import net.wanho.fitai.modules.member.service.MemberLevelBenefitServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemberLevelBenefitServiceImpl extends MPJBaseServiceImpl<MemberLevelBenefitMapper, MemberLevelBenefit> implements MemberLevelBenefitServiceI {

    private final Converter converter;

    @Override
    public IPage<MemberLevelBenefitVO> findByPage(MemberLevelBenefitQueryDTO queryDto) {

        IPage<MemberLevelBenefit> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemberLevelBenefit> wrapper = new LambdaQueryWrapper<>();
        IPage<MemberLevelBenefit> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemberLevelBenefitVO.class));

    }

    @Override
    public void add(MemberLevelBenefitAddDTO addDto) {
        MemberLevelBenefit memberlevelbenefit = converter.convert(addDto, MemberLevelBenefit.class);
        this.save(memberlevelbenefit);
    }

    @Override
    public void update(MemberLevelBenefitUpdateDTO updateDto) {
        MemberLevelBenefit memberlevelbenefit = converter.convert(updateDto, MemberLevelBenefit.class);
        this.updateById(memberlevelbenefit);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemberLevelBenefitVO findById(Long id) {
        MemberLevelBenefit entity = this.getById(id);
        return converter.convert(entity, MemberLevelBenefitVO.class);
    }
}
