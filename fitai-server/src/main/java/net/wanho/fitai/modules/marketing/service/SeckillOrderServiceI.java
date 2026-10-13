package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.SeckillOrderAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillOrderQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillOrderUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.SeckillOrder;
import net.wanho.fitai.modules.marketing.model.vo.SeckillOrderVO;

public interface SeckillOrderServiceI extends MPJBaseService<SeckillOrder> {
    IPage<SeckillOrderVO> findByPage(SeckillOrderQueryDTO queryDto);

    void add(SeckillOrderAddDTO addDto);

    void update(SeckillOrderUpdateDTO updateDto);

    void delete(String id);

    SeckillOrderVO findById(Long id);

}
