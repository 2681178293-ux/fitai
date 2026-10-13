package net.wanho.fitai.modules.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.community.model.dto.CmsCommentLikeAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentLikeQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentLikeUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsCommentLike;
import net.wanho.fitai.modules.community.model.vo.CmsCommentLikeVO;

public interface CmsCommentLikeServiceI extends MPJBaseService<CmsCommentLike> {
    IPage<CmsCommentLikeVO> findByPage(CmsCommentLikeQueryDTO queryDto);

    void add(CmsCommentLikeAddDTO addDto);

    void update(CmsCommentLikeUpdateDTO updateDto);

    void delete(String id);

    CmsCommentLikeVO findById(Long id);

}
