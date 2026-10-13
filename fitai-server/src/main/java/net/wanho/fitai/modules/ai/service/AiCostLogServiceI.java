package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiCostLogAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiCostLogQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiCostLogUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiCostLog;
import net.wanho.fitai.modules.ai.model.vo.AiCostLogVO;

public interface AiCostLogServiceI extends MPJBaseService<AiCostLog> {
    IPage<AiCostLogVO> findByPage(AiCostLogQueryDTO queryDto);

    void add(AiCostLogAddDTO addDto);

    void update(AiCostLogUpdateDTO updateDto);

    void delete(String id);

    AiCostLogVO findById(Long id);

}
