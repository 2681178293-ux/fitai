package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdStockAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdStockQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdStockUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdStock;
import net.wanho.fitai.modules.mall.model.vo.PrdStockVO;

public interface PrdStockServiceI extends MPJBaseService<PrdStock> {
    IPage<PrdStockVO> findByPage(PrdStockQueryDTO queryDto);

    void add(PrdStockAddDTO addDto);

    void update(PrdStockUpdateDTO updateDto);

    void delete(String id);

    PrdStockVO findById(Long id);

}
