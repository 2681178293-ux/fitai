package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsTraceAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsTraceQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsTraceUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdLogisticsTrace;
import net.wanho.fitai.modules.mall.model.vo.OrdLogisticsTraceVO;

public interface OrdLogisticsTraceServiceI extends MPJBaseService<OrdLogisticsTrace> {
    IPage<OrdLogisticsTraceVO> findByPage(OrdLogisticsTraceQueryDTO queryDto);

    void add(OrdLogisticsTraceAddDTO addDto);

    void update(OrdLogisticsTraceUpdateDTO updateDto);

    void delete(String id);

    OrdLogisticsTraceVO findById(Long id);

}
