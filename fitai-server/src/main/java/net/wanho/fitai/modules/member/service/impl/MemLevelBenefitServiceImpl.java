package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemLevelBenefitMapper;
import net.wanho.fitai.modules.member.model.dto.MemLevelBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemLevelBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemLevelBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemLevelBenefit;
import net.wanho.fitai.modules.member.model.vo.MemLevelBenefitVO;
import net.wanho.fitai.modules.member.service.MemLevelBenefitServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemLevelBenefitServiceImpl extends MPJBaseServiceImpl<MemLevelBenefitMapper, MemLevelBenefit> implements MemLevelBenefitServiceI {

    private final Converter converter;

    @Override
    public IPage<MemLevelBenefitVO> findByPage(MemLevelBenefitQueryDTO queryDto) {

        IPage<MemLevelBenefit> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemLevelBenefit> wrapper = new LambdaQueryWrapper<>();
        IPage<MemLevelBenefit> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemLevelBenefitVO.class));

    }

    @Override
    public void add(MemLevelBenefitAddDTO addDto) {
        MemLevelBenefit memlevelbenefit = converter.convert(addDto, MemLevelBenefit.class);
        this.save(memlevelbenefit);
    }

    @Override
    public void update(MemLevelBenefitUpdateDTO updateDto) {
        MemLevelBenefit memlevelbenefit = converter.convert(updateDto, MemLevelBenefit.class);
        this.updateById(memlevelbenefit);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemLevelBenefitVO findById(Long id) {
        MemLevelBenefit entity = this.getById(id);
        return converter.convert(entity, MemLevelBenefitVO.class);
    }
}
