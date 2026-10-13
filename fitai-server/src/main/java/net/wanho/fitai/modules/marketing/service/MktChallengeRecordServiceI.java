package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeRecordAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeRecordQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeRecordUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktChallengeRecord;
import net.wanho.fitai.modules.marketing.model.vo.MktChallengeRecordVO;

public interface MktChallengeRecordServiceI extends MPJBaseService<MktChallengeRecord> {
    IPage<MktChallengeRecordVO> findByPage(MktChallengeRecordQueryDTO queryDto);

    void add(MktChallengeRecordAddDTO addDto);

    void update(MktChallengeRecordUpdateDTO updateDto);

    void delete(String id);

    MktChallengeRecordVO findById(Long id);

}
