package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktFlashSale;
import net.wanho.fitai.modules.marketing.model.vo.MktFlashSaleVO;

public interface MktFlashSaleServiceI extends MPJBaseService<MktFlashSale> {
    IPage<MktFlashSaleVO> findByPage(MktFlashSaleQueryDTO queryDto);

    void add(MktFlashSaleAddDTO addDto);

    void update(MktFlashSaleUpdateDTO updateDto);

    void delete(String id);

    MktFlashSaleVO findById(Long id);

}
