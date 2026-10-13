package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.OrdMemberOrderAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdMemberOrderQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdMemberOrderUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdMemberOrder;
import net.wanho.fitai.modules.mall.model.vo.OrdMemberOrderVO;

public interface OrdMemberOrderServiceI extends MPJBaseService<OrdMemberOrder> {
    IPage<OrdMemberOrderVO> findByPage(OrdMemberOrderQueryDTO queryDto);

    void add(OrdMemberOrderAddDTO addDto);

    void update(OrdMemberOrderUpdateDTO updateDto);

    void delete(String id);

    OrdMemberOrderVO findById(Long id);

}
