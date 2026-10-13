package net.wanho.fitai.modules.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.community.model.dto.*;
import net.wanho.fitai.modules.community.model.po.CommunityModeration;
import net.wanho.fitai.modules.community.model.vo.CommunityModerationVO;

public interface CommunityModerationServiceI extends MPJBaseService<CommunityModeration> {
    IPage<CommunityModerationVO> findByPage(CommunityModerationQueryDTO queryDto);

    void add(CommunityModerationAddDTO addDto);

    void update(CommunityModerationUpdateDTO updateDto);

    void delete(String id);

    CommunityModerationVO findById(Long id);

    void reject(CommunityModerationRejectDTO rejectDto);

    IPage<CommunityModerationVO> historyPage(CommunityModerationHistoryQueryDTO queryDto);
}
