package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.OrdRefundAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdRefundQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdRefundUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdRefund;
import net.wanho.fitai.modules.mall.model.vo.OrdRefundVO;

public interface OrdRefundServiceI extends MPJBaseService<OrdRefund> {
    IPage<OrdRefundVO> findByPage(OrdRefundQueryDTO queryDto);

    void add(OrdRefundAddDTO addDto);

    void update(OrdRefundUpdateDTO updateDto);

    void delete(String id);

    OrdRefundVO findById(Long id);

}
