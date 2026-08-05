package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysEntryDailyAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysEntryDailyQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysEntryDailyUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysEntryDaily;
import net.wanho.fitai.modules.system.model.vo.SysEntryDailyVO;

import java.time.LocalDate;

public interface SysEntryDailyServiceI extends MPJBaseService<SysEntryDaily> {
    IPage<SysEntryDailyVO> findByPage(SysEntryDailyQueryDTO queryDto);

    void add(SysEntryDailyAddDTO addDto);

    void update(SysEntryDailyUpdateDTO updateDto);

    void delete(LocalDate statDate, String channel);

    SysEntryDailyVO findById(LocalDate statDate, String channel);
}
