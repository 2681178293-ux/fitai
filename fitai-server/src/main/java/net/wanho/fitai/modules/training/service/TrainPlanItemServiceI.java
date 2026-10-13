package net.wanho.fitai.modules.training.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.training.model.dto.TrainPlanItemAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanItemQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanItemUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainPlanItem;
import net.wanho.fitai.modules.training.model.vo.TrainPlanItemVO;

public interface TrainPlanItemServiceI extends MPJBaseService<TrainPlanItem> {
    IPage<TrainPlanItemVO> findByPage(TrainPlanItemQueryDTO queryDto);

    void add(TrainPlanItemAddDTO addDto);

    void update(TrainPlanItemUpdateDTO updateDto);

    void delete(String id);

    TrainPlanItemVO findById(Long id);

}
