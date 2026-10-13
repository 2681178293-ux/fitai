package net.wanho.fitai.modules.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.community.model.dto.CmsPostAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsPost;
import net.wanho.fitai.modules.community.model.vo.CmsPostVO;

public interface CmsPostServiceI extends MPJBaseService<CmsPost> {
    IPage<CmsPostVO> findByPage(CmsPostQueryDTO queryDto);

    void add(CmsPostAddDTO addDto);

    void update(CmsPostUpdateDTO updateDto);

    void delete(String id);

    CmsPostVO findById(Long id);

}
