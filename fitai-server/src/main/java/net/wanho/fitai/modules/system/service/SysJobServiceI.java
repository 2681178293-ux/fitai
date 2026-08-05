package net.wanho.fitai.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.system.model.dto.SysJobAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysJobQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysJobUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysJob;
import net.wanho.fitai.modules.system.model.vo.SysJobVO;

public interface SysJobServiceI extends MPJBaseService<SysJob> {
    IPage<SysJobVO> findByPage(SysJobQueryDTO queryDto);

    void add(SysJobAddDTO addDto);

    void update(SysJobUpdateDTO updateDto);

    void delete(String id);

    SysJobVO findById(Long id);
}
