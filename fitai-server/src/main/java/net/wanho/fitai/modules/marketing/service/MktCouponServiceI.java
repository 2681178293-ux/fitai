package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktCoupon;
import net.wanho.fitai.modules.marketing.model.vo.MktCouponVO;

public interface MktCouponServiceI extends MPJBaseService<MktCoupon> {
    IPage<MktCouponVO> findByPage(MktCouponQueryDTO queryDto);

    void add(MktCouponAddDTO addDto);

    void update(MktCouponUpdateDTO updateDto);

    void delete(String id);

    MktCouponVO findById(Long id);

}
