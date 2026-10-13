package net.wanho.fitai.modules.diet.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.diet.model.dto.DietPlanAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietPlan;
import net.wanho.fitai.modules.diet.model.vo.DietPlanVO;

public interface DietPlanServiceI extends MPJBaseService<DietPlan> {
    IPage<DietPlanVO> findByPage(DietPlanQueryDTO queryDto);

    void add(DietPlanAddDTO addDto);

    void update(DietPlanUpdateDTO updateDto);

    void delete(String id);

    DietPlanVO findById(Long id);

}
