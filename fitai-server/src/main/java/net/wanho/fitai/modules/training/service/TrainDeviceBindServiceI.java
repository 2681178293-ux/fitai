package net.wanho.fitai.modules.training.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceBindAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceBindQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceBindUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainDeviceBind;
import net.wanho.fitai.modules.training.model.vo.TrainDeviceBindVO;

public interface TrainDeviceBindServiceI extends MPJBaseService<TrainDeviceBind> {
    IPage<TrainDeviceBindVO> findByPage(TrainDeviceBindQueryDTO queryDto);

    void add(TrainDeviceBindAddDTO addDto);

    void update(TrainDeviceBindUpdateDTO updateDto);

    void delete(String id);

    TrainDeviceBindVO findById(Long id);

}
