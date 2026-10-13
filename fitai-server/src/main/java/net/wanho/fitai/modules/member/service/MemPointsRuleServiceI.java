package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemPointsRuleAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsRuleQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsRuleUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemPointsRule;
import net.wanho.fitai.modules.member.model.vo.MemPointsRuleVO;

public interface MemPointsRuleServiceI extends MPJBaseService<MemPointsRule> {
    IPage<MemPointsRuleVO> findByPage(MemPointsRuleQueryDTO queryDto);

    void add(MemPointsRuleAddDTO addDto);

    void update(MemPointsRuleUpdateDTO updateDto);

    void delete(String id);

    MemPointsRuleVO findById(Long id);

}
