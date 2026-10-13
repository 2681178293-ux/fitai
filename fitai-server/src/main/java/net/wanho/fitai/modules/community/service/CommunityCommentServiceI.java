package net.wanho.fitai.modules.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.community.model.dto.CommunityCommentAddDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityCommentQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityCommentUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CommunityComment;
import net.wanho.fitai.modules.community.model.vo.CommunityCommentVO;

public interface CommunityCommentServiceI extends MPJBaseService<CommunityComment> {
    IPage<CommunityCommentVO> findByPage(CommunityCommentQueryDTO queryDto);

    void add(CommunityCommentAddDTO addDto);

    void update(CommunityCommentUpdateDTO updateDto);

    void delete(String id);

    CommunityCommentVO findById(Long id);

    void top(Long id, Integer top);
}
