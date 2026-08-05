package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysConfigAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysConfigQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysConfigUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysConfig;

public interface SysConfigServiceI extends MPJBaseService<SysConfig> {
    IPage<SysConfig> findByPage(SysConfigQueryDTO queryDto);

    void add(SysConfigAddDTO addDto);

    void update(SysConfigUpdateDTO updateDto);

    void delete(String id);

}
