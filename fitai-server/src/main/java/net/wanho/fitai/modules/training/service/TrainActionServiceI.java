package net.wanho.fitai.modules.training.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.training.model.dto.TrainActionAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainActionQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainActionUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainAction;
import net.wanho.fitai.modules.training.model.vo.TrainActionVO;

public interface TrainActionServiceI extends MPJBaseService<TrainAction> {
    IPage<TrainActionVO> findByPage(TrainActionQueryDTO queryDto);

    void add(TrainActionAddDTO addDto);

    void update(TrainActionUpdateDTO updateDto);

    void delete(String id);

    TrainActionVO findById(Long id);

}
