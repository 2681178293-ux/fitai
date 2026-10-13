package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdSpu;
import net.wanho.fitai.modules.mall.model.vo.PrdSpuVO;

public interface PrdSpuServiceI extends MPJBaseService<PrdSpu> {
    IPage<PrdSpuVO> findByPage(PrdSpuQueryDTO queryDto);

    void add(PrdSpuAddDTO addDto);

    void update(PrdSpuUpdateDTO updateDto);

    void delete(String id);

    PrdSpuVO findById(Long id);

    void updateProductStatus(Long spuId, String status);
}
