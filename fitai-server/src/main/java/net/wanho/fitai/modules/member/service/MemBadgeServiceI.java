package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemBadgeAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemBadgeQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemBadgeUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemBadge;
import net.wanho.fitai.modules.member.model.vo.MemBadgeVO;

public interface MemBadgeServiceI extends MPJBaseService<MemBadge> {
    IPage<MemBadgeVO> findByPage(MemBadgeQueryDTO queryDto);

    void add(MemBadgeAddDTO addDto);

    void update(MemBadgeUpdateDTO updateDto);

    void delete(String id);

    MemBadgeVO findById(Long id);

}
