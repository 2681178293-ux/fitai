package net.wanho.fitai.modules.training.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.training.model.dto.TrainPlanAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainPlan;
import net.wanho.fitai.modules.training.model.vo.TrainPlanVO;

public interface TrainPlanServiceI extends MPJBaseService<TrainPlan> {
    IPage<TrainPlanVO> findByPage(TrainPlanQueryDTO queryDto);

    void add(TrainPlanAddDTO addDto);

    void update(TrainPlanUpdateDTO updateDto);

    void delete(String id);

    TrainPlanVO findById(Long id);

}
