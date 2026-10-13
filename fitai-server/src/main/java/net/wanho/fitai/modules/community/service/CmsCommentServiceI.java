package net.wanho.fitai.modules.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.community.model.dto.CmsCommentAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsComment;
import net.wanho.fitai.modules.community.model.vo.CmsCommentVO;

public interface CmsCommentServiceI extends MPJBaseService<CmsComment> {
    IPage<CmsCommentVO> findByPage(CmsCommentQueryDTO queryDto);

    void add(CmsCommentAddDTO addDto);

    void update(CmsCommentUpdateDTO updateDto);

    void delete(String id);

    CmsCommentVO findById(Long id);

}
