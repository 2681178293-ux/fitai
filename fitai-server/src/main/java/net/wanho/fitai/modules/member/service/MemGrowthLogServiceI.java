package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemGrowthLogAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemGrowthLogQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemGrowthLogUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemGrowthLog;
import net.wanho.fitai.modules.member.model.vo.MemGrowthLogVO;

public interface MemGrowthLogServiceI extends MPJBaseService<MemGrowthLog> {
    IPage<MemGrowthLogVO> findByPage(MemGrowthLogQueryDTO queryDto);

    void add(MemGrowthLogAddDTO addDto);

    void update(MemGrowthLogUpdateDTO updateDto);

    void delete(String id);

    MemGrowthLogVO findById(Long id);

}
