package net.wanho.fitai.modules.training.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.training.model.dto.TrainRecordAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainRecord;
import net.wanho.fitai.modules.training.model.vo.TrainRecordVO;

public interface TrainRecordServiceI extends MPJBaseService<TrainRecord> {
    IPage<TrainRecordVO> findByPage(TrainRecordQueryDTO queryDto);

    void add(TrainRecordAddDTO addDto);

    void update(TrainRecordUpdateDTO updateDto);

    void delete(String id);

    TrainRecordVO findById(Long id);

}
