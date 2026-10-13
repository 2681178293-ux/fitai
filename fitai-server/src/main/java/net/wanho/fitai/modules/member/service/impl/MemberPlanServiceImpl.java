package net.wanho.fitai.modules.member.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemberPlanMapper;
import net.wanho.fitai.modules.member.model.dto.MemberPlanAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberPlanUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemberPlan;
import net.wanho.fitai.modules.member.model.vo.MemberPlanVO;
import net.wanho.fitai.modules.member.service.MemberPlanServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPlanServiceImpl extends MPJBaseServiceImpl<MemberPlanMapper, MemberPlan> implements MemberPlanServiceI {

    private final Converter converter;

    @Override
    public List<MemberPlanVO> findByPage() {
        LambdaQueryWrapper<MemberPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MemberPlan::getSortOrder)
                .orderByAsc(MemberPlan::getId);
        return this.list(wrapper).stream()
                .map(item -> converter.convert(item, MemberPlanVO.class))
                .toList();
    }

    @Override
    public void add(MemberPlanAddDTO addDto) {
        MemberPlan memberplan = converter.convert(addDto, MemberPlan.class);
        this.save(memberplan);
    }

    @Override
    public void update(MemberPlanUpdateDTO updateDto) {
        MemberPlan memberplan = converter.convert(updateDto, MemberPlan.class);
        this.updateById(memberplan);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemberPlanVO findById(Long id) {
        MemberPlan entity = this.getById(id);
        return converter.convert(entity, MemberPlanVO.class);
    }
}
