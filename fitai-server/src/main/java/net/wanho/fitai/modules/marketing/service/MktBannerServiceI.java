package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.MktBannerAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktBannerQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktBannerUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktBanner;
import net.wanho.fitai.modules.marketing.model.vo.MktBannerVO;

public interface MktBannerServiceI extends MPJBaseService<MktBanner> {
    IPage<MktBannerVO> findByPage(MktBannerQueryDTO queryDto);

    void add(MktBannerAddDTO addDto);

    void update(MktBannerUpdateDTO updateDto);

    void delete(String id);

    MktBannerVO findById(Long id);

}
