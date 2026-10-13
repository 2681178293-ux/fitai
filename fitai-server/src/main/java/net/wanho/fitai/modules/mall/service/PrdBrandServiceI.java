package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdBrandAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdBrandQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdBrandUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdBrand;
import net.wanho.fitai.modules.mall.model.vo.PrdBrandVO;

import java.util.List;

public interface PrdBrandServiceI extends MPJBaseService<PrdBrand> {
    IPage<PrdBrandVO> findByPage(PrdBrandQueryDTO queryDto);

    void add(PrdBrandAddDTO addDto);

    void update(PrdBrandUpdateDTO updateDto);

    void delete(String id);

    PrdBrandVO findById(Long id);

    List<PrdBrandVO> getBrandSelect();
}
