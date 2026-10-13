package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemCustomerBadgeAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemCustomerBadgeQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemCustomerBadgeUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemCustomerBadge;
import net.wanho.fitai.modules.member.model.vo.MemCustomerBadgeVO;

public interface MemCustomerBadgeServiceI extends MPJBaseService<MemCustomerBadge> {
    IPage<MemCustomerBadgeVO> findByPage(MemCustomerBadgeQueryDTO queryDto);

    void add(MemCustomerBadgeAddDTO addDto);

    void update(MemCustomerBadgeUpdateDTO updateDto);

    void delete(String id);

    MemCustomerBadgeVO findById(Long id);

}
