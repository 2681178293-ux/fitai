package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysLoginLogQueryDTO;
import net.wanho.fitai.modules.system.model.po.SysLoginLog;
import net.wanho.fitai.modules.system.model.vo.SysLoginLogVO;

public interface SysLoginLogServiceI extends MPJBaseService<SysLoginLog> {
    IPage<SysLoginLogVO> findByPage(SysLoginLogQueryDTO queryDto);

    void delete(String id);

    SysLoginLogVO findById(Long id);

    void recordLoginLog(String userName,String userType,String clientType, String status, String msg);
}
