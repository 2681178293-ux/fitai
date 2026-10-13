package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleSkuAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleSkuQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleSkuUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktFlashSaleSku;
import net.wanho.fitai.modules.marketing.model.vo.MktFlashSaleSkuVO;

public interface MktFlashSaleSkuServiceI extends MPJBaseService<MktFlashSaleSku> {
    IPage<MktFlashSaleSkuVO> findByPage(MktFlashSaleSkuQueryDTO queryDto);

    void add(MktFlashSaleSkuAddDTO addDto);

    void update(MktFlashSaleSkuUpdateDTO updateDto);

    void delete(String id);

    MktFlashSaleSkuVO findById(Long id);

}
