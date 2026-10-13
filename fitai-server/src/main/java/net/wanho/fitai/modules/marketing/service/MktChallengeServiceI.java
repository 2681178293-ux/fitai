package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktChallenge;
import net.wanho.fitai.modules.marketing.model.vo.MktChallengeVO;

public interface MktChallengeServiceI extends MPJBaseService<MktChallenge> {
    IPage<MktChallengeVO> findByPage(MktChallengeQueryDTO queryDto);

    void add(MktChallengeAddDTO addDto);

    void update(MktChallengeUpdateDTO updateDto);

    void delete(String id);

    MktChallengeVO findById(Long id);

}
