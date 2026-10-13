package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUserAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUserQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUserUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktCouponUser;
import net.wanho.fitai.modules.marketing.model.vo.MktCouponUserVO;

public interface MktCouponUserServiceI extends MPJBaseService<MktCouponUser> {
    IPage<MktCouponUserVO> findByPage(MktCouponUserQueryDTO queryDto);

    void add(MktCouponUserAddDTO addDto);

    void update(MktCouponUserUpdateDTO updateDto);

    void delete(String id);

    MktCouponUserVO findById(Long id);

}
