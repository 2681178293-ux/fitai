package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdOrder;
import net.wanho.fitai.modules.mall.model.vo.OrdOrderVO;

public interface OrdOrderServiceI extends MPJBaseService<OrdOrder> {
    IPage<OrdOrderVO> findByPage(OrdOrderQueryDTO queryDto);

    void add(OrdOrderAddDTO addDto);

    void update(OrdOrderUpdateDTO updateDto);

    void delete(String id);

    OrdOrderVO findById(Long id);

}
