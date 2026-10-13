package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdCartAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCartQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCartUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdCart;
import net.wanho.fitai.modules.mall.model.vo.PrdCartVO;

public interface PrdCartServiceI extends MPJBaseService<PrdCart> {
    IPage<PrdCartVO> findByPage(PrdCartQueryDTO queryDto);

    void add(PrdCartAddDTO addDto);

    void update(PrdCartUpdateDTO updateDto);

    void delete(String id);

    PrdCartVO findById(Long id);

}
