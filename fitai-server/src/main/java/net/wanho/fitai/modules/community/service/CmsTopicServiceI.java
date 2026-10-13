package net.wanho.fitai.modules.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.community.model.dto.CmsTopicAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsTopicQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsTopicUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsTopic;
import net.wanho.fitai.modules.community.model.vo.CmsTopicVO;

public interface CmsTopicServiceI extends MPJBaseService<CmsTopic> {
    IPage<CmsTopicVO> findByPage(CmsTopicQueryDTO queryDto);

    void add(CmsTopicAddDTO addDto);

    void update(CmsTopicUpdateDTO updateDto);

    void delete(String id);

    CmsTopicVO findById(Long id);

}
