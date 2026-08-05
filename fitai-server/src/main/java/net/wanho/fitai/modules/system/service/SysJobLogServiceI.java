package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysJobLogQueryDTO;
import net.wanho.fitai.modules.system.model.po.SysJobLog;
import net.wanho.fitai.modules.system.model.vo.SysJobLogVO;

public interface SysJobLogServiceI extends MPJBaseService<SysJobLog> {
    IPage<SysJobLogVO> findByPage(SysJobLogQueryDTO queryDto);

    void delete(String id);

    SysJobLogVO findById(Long id);
}
