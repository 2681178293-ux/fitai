package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysNoticeRead;
import net.wanho.fitai.modules.system.model.vo.SysNoticeReadVO;

public interface SysNoticeReadServiceI extends MPJBaseService<SysNoticeRead> {
    IPage<SysNoticeReadVO> findByPage(SysNoticeReadQueryDTO queryDto);

    void add(SysNoticeReadAddDTO addDto);

    void update(SysNoticeReadUpdateDTO updateDto);

    void delete(String id);

    SysNoticeReadVO findById(Long id);
}
