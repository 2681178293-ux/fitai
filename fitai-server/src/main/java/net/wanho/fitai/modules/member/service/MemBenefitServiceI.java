package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemBenefit;
import net.wanho.fitai.modules.member.model.vo.MemBenefitVO;

public interface MemBenefitServiceI extends MPJBaseService<MemBenefit> {
    IPage<MemBenefitVO> findByPage(MemBenefitQueryDTO queryDto);

    void add(MemBenefitAddDTO addDto);

    void update(MemBenefitUpdateDTO updateDto);

    void delete(String id);

    MemBenefitVO findById(Long id);

}
