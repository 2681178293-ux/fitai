package net.wanho.fitai.modules.training.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.training.model.dto.TrainRecordItemAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordItemQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordItemUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainRecordItem;
import net.wanho.fitai.modules.training.model.vo.TrainRecordItemVO;

public interface TrainRecordItemServiceI extends MPJBaseService<TrainRecordItem> {
    IPage<TrainRecordItemVO> findByPage(TrainRecordItemQueryDTO queryDto);

    void add(TrainRecordItemAddDTO addDto);

    void update(TrainRecordItemUpdateDTO updateDto);

    void delete(String id);

    TrainRecordItemVO findById(Long id);

}
