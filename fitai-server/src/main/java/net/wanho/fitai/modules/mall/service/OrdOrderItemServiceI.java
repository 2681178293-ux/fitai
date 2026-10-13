package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderItemAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderItemQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderItemUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdOrderItem;
import net.wanho.fitai.modules.mall.model.vo.OrdOrderItemVO;

public interface OrdOrderItemServiceI extends MPJBaseService<OrdOrderItem> {
    IPage<OrdOrderItemVO> findByPage(OrdOrderItemQueryDTO queryDto);

    void add(OrdOrderItemAddDTO addDto);

    void update(OrdOrderItemUpdateDTO updateDto);

    void delete(String id);

    OrdOrderItemVO findById(Long id);

}
