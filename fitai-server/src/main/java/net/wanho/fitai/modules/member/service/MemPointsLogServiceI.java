package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemPointsLogAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsLogQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsLogUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemPointsLog;
import net.wanho.fitai.modules.member.model.vo.MemPointsLogVO;

public interface MemPointsLogServiceI extends MPJBaseService<MemPointsLog> {
    IPage<MemPointsLogVO> findByPage(MemPointsLogQueryDTO queryDto);

    void add(MemPointsLogAddDTO addDto);

    void update(MemPointsLogUpdateDTO updateDto);

    void delete(String id);

    MemPointsLogVO findById(Long id);

}
