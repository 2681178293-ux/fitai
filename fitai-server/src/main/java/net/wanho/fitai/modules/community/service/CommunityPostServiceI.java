package net.wanho.fitai.modules.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.community.model.dto.CommunityPostAddDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityPostQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityPostUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CommunityPost;
import net.wanho.fitai.modules.community.model.vo.CommunityPostVO;

public interface CommunityPostServiceI extends MPJBaseService<CommunityPost> {
    IPage<CommunityPostVO> findByPage(CommunityPostQueryDTO queryDto);

    void add(CommunityPostAddDTO addDto);

    void update(CommunityPostUpdateDTO updateDto);

    void delete(String id);

    CommunityPostVO findById(Long id);

}
