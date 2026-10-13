package net.wanho.fitai.modules.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.community.model.dto.CmsModerationAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsModerationQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsModerationUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsModeration;
import net.wanho.fitai.modules.community.model.vo.CmsModerationVO;

public interface CmsModerationServiceI extends MPJBaseService<CmsModeration> {
    IPage<CmsModerationVO> findByPage(CmsModerationQueryDTO queryDto);

    void add(CmsModerationAddDTO addDto);

    void update(CmsModerationUpdateDTO updateDto);

    void delete(String id);

    CmsModerationVO findById(Long id);

}
