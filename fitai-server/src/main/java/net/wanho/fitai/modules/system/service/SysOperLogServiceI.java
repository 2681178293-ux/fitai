package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysOperLogQueryDTO;
import net.wanho.fitai.modules.system.model.po.SysOperLog;
import net.wanho.fitai.modules.system.model.vo.SysOperLogVO;

public interface SysOperLogServiceI extends MPJBaseService<SysOperLog> {
    IPage<SysOperLogVO> findByPage(SysOperLogQueryDTO queryDto);

    void delete(String id);

    SysOperLogVO findById(Long id);

    void recordOperLog(SysOperLog sysOperLog);
}
