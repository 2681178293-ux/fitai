package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemCheckinAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemCheckinQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemCheckinUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemCheckin;
import net.wanho.fitai.modules.member.model.vo.MemCheckinVO;

public interface MemCheckinServiceI extends MPJBaseService<MemCheckin> {
    IPage<MemCheckinVO> findByPage(MemCheckinQueryDTO queryDto);

    void add(MemCheckinAddDTO addDto);

    void update(MemCheckinUpdateDTO updateDto);

    void delete(String id);

    MemCheckinVO findById(Long id);

}
