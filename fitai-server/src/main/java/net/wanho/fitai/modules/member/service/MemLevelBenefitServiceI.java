package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemLevelBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemLevelBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemLevelBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemLevelBenefit;
import net.wanho.fitai.modules.member.model.vo.MemLevelBenefitVO;

public interface MemLevelBenefitServiceI extends MPJBaseService<MemLevelBenefit> {
    IPage<MemLevelBenefitVO> findByPage(MemLevelBenefitQueryDTO queryDto);

    void add(MemLevelBenefitAddDTO addDto);

    void update(MemLevelBenefitUpdateDTO updateDto);

    void delete(String id);

    MemLevelBenefitVO findById(Long id);

}
