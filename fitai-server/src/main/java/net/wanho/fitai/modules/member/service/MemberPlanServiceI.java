package net.wanho.fitai.modules.member.service;


import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemberPlanAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberPlanUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemberPlan;
import net.wanho.fitai.modules.member.model.vo.MemberPlanVO;

import java.util.List;

public interface MemberPlanServiceI extends MPJBaseService<MemberPlan> {
    List<MemberPlanVO> findByPage();

    void add(MemberPlanAddDTO addDto);

    void update(MemberPlanUpdateDTO updateDto);

    void delete(String id);

    MemberPlanVO findById(Long id);

}
