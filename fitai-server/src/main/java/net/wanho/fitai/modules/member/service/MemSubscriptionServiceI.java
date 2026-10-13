package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemSubscriptionAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemSubscriptionQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemSubscriptionUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemSubscription;
import net.wanho.fitai.modules.member.model.vo.MemSubscriptionVO;

public interface MemSubscriptionServiceI extends MPJBaseService<MemSubscription> {
    IPage<MemSubscriptionVO> findByPage(MemSubscriptionQueryDTO queryDto);

    void add(MemSubscriptionAddDTO addDto);

    void update(MemSubscriptionUpdateDTO updateDto);

    void delete(String id);

    MemSubscriptionVO findById(Long id);

}
