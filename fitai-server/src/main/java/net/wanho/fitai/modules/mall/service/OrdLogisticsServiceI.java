package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdLogistics;
import net.wanho.fitai.modules.mall.model.vo.OrdLogisticsVO;

public interface OrdLogisticsServiceI extends MPJBaseService<OrdLogistics> {
    IPage<OrdLogisticsVO> findByPage(OrdLogisticsQueryDTO queryDto);

    void add(OrdLogisticsAddDTO addDto);

    void update(OrdLogisticsUpdateDTO updateDto);

    void delete(String id);

    OrdLogisticsVO findById(Long id);

}
