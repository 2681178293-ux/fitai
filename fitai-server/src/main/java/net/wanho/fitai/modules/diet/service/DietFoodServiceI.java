package net.wanho.fitai.modules.diet.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.diet.model.dto.DietFoodAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietFoodQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietFoodUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietFood;
import net.wanho.fitai.modules.diet.model.vo.DietFoodVO;

public interface DietFoodServiceI extends MPJBaseService<DietFood> {
    IPage<DietFoodVO> findByPage(DietFoodQueryDTO queryDto);

    void add(DietFoodAddDTO addDto);

    void update(DietFoodUpdateDTO updateDto);

    void delete(String id);

    DietFoodVO findById(Long id);

}
