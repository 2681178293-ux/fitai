package net.wanho.fitai.modules.diet.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.diet.model.dto.DietPlanItemAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanItemQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanItemUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietPlanItem;
import net.wanho.fitai.modules.diet.model.vo.DietPlanItemVO;

public interface DietPlanItemServiceI extends MPJBaseService<DietPlanItem> {
    IPage<DietPlanItemVO> findByPage(DietPlanItemQueryDTO queryDto);

    void add(DietPlanItemAddDTO addDto);

    void update(DietPlanItemUpdateDTO updateDto);

    void delete(String id);

    DietPlanItemVO findById(Long id);

}
