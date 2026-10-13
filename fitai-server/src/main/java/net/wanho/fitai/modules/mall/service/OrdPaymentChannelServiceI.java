package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentChannelAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentChannelQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentChannelUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdPaymentChannel;
import net.wanho.fitai.modules.mall.model.vo.OrdPaymentChannelVO;

public interface OrdPaymentChannelServiceI extends MPJBaseService<OrdPaymentChannel> {
    IPage<OrdPaymentChannelVO> findByPage(OrdPaymentChannelQueryDTO queryDto);

    void add(OrdPaymentChannelAddDTO addDto);

    void update(OrdPaymentChannelUpdateDTO updateDto);

    void delete(String id);

    OrdPaymentChannelVO findById(Long id);

}
