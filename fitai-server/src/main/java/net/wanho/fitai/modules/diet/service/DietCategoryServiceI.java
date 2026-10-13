package net.wanho.fitai.modules.diet.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.diet.model.dto.DietCategoryAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietCategoryQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietCategoryUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietCategory;
import net.wanho.fitai.modules.diet.model.vo.DietCategoryVO;

public interface DietCategoryServiceI extends MPJBaseService<DietCategory> {
    IPage<DietCategoryVO> findByPage(DietCategoryQueryDTO queryDto);

    void add(DietCategoryAddDTO addDto);

    void update(DietCategoryUpdateDTO updateDto);

    void delete(String id);

    DietCategoryVO findById(Long id);

}
