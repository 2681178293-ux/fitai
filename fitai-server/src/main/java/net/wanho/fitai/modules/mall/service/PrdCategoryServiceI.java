package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdCategory;
import net.wanho.fitai.modules.mall.model.vo.PrdCategoryVO;

import java.util.List;

public interface PrdCategoryServiceI extends MPJBaseService<PrdCategory> {
    IPage<PrdCategoryVO> findByPage(PrdCategoryQueryDTO queryDto);

    void add(PrdCategoryAddDTO addDto);

    void update(PrdCategoryUpdateDTO updateDto);

    void delete(String id);

    PrdCategoryVO findById(Long id);

    List<PrdCategoryVO> tree(PrdCategoryQueryDTO queryDto);

    void updateStatus(Long categoryId, String status);
}
