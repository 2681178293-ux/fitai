package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemberLevelBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberLevelBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemberLevelBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemberLevelBenefit;
import net.wanho.fitai.modules.member.model.vo.MemberLevelBenefitVO;

public interface MemberLevelBenefitServiceI extends MPJBaseService<MemberLevelBenefit> {
    IPage<MemberLevelBenefitVO> findByPage(MemberLevelBenefitQueryDTO queryDto);

    void add(MemberLevelBenefitAddDTO addDto);

    void update(MemberLevelBenefitUpdateDTO updateDto);

    void delete(String id);

    MemberLevelBenefitVO findById(Long id);

}
