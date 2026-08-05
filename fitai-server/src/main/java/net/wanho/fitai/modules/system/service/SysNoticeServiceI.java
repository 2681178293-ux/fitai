package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysNoticeAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysNotice;
import net.wanho.fitai.modules.system.model.vo.SysNoticeVO;

public interface SysNoticeServiceI extends MPJBaseService<SysNotice> {
    IPage<SysNoticeVO> findByPage(SysNoticeQueryDTO queryDto);

    void add(SysNoticeAddDTO addDto);

    void update(SysNoticeUpdateDTO updateDto);

    void delete(String id);

    SysNoticeVO findById(Long id);
}
