package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdSku;
import net.wanho.fitai.modules.mall.model.vo.PrdSkuVO;

public interface PrdSkuServiceI extends MPJBaseService<PrdSku> {
    IPage<PrdSkuVO> findByPage(PrdSkuQueryDTO queryDto);

    void add(PrdSkuAddDTO addDto);

    void update(PrdSkuUpdateDTO updateDto);

    void delete(String id);

    PrdSkuVO findById(Long id);

}
