package net.wanho.fitai.modules.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.community.model.dto.CmsPostLikeAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostLikeQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostLikeUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsPostLike;
import net.wanho.fitai.modules.community.model.vo.CmsPostLikeVO;

public interface CmsPostLikeServiceI extends MPJBaseService<CmsPostLike> {
    IPage<CmsPostLikeVO> findByPage(CmsPostLikeQueryDTO queryDto);

    void add(CmsPostLikeAddDTO addDto);

    void update(CmsPostLikeUpdateDTO updateDto);

    void delete(String id);

    CmsPostLikeVO findById(Long id);

}
