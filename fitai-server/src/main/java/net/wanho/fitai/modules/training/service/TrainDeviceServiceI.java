package net.wanho.fitai.modules.training.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceUpdateDTO;
import net.wanho.fitai.modules.training.model.po.TrainDevice;
import net.wanho.fitai.modules.training.model.vo.TrainDeviceVO;

public interface TrainDeviceServiceI extends MPJBaseService<TrainDevice> {
    IPage<TrainDeviceVO> findByPage(TrainDeviceQueryDTO queryDto);

    void add(TrainDeviceAddDTO addDto);

    void update(TrainDeviceUpdateDTO updateDto);

    void delete(String id);

    TrainDeviceVO findById(Long id);

}
