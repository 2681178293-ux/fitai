package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemberBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemberBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemberBenefit;
import net.wanho.fitai.modules.member.model.vo.MemberBenefitVO;

public interface MemberBenefitServiceI extends MPJBaseService<MemberBenefit> {
    IPage<MemberBenefitVO> findByPage(MemberBenefitQueryDTO queryDto);

    void add(MemberBenefitAddDTO addDto);

    void update(MemberBenefitUpdateDTO updateDto);

    void delete(String id);

    MemberBenefitVO findById(Long id);

}
