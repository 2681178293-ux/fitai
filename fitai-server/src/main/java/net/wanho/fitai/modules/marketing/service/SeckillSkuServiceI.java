package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.SeckillSkuAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillSkuQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillSkuUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.SeckillSku;
import net.wanho.fitai.modules.marketing.model.vo.SeckillSkuVO;

public interface SeckillSkuServiceI extends MPJBaseService<SeckillSku> {
    IPage<SeckillSkuVO> findByPage(SeckillSkuQueryDTO queryDto);

    void add(SeckillSkuAddDTO addDto);

    void update(SeckillSkuUpdateDTO updateDto);

    void delete(String id);

    SeckillSkuVO findById(Long id);

}
