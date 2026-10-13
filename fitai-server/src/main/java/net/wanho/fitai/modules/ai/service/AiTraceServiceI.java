package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiTraceAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTraceQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTraceUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiTrace;
import net.wanho.fitai.modules.ai.model.vo.AiTraceVO;

public interface AiTraceServiceI extends MPJBaseService<AiTrace> {
    IPage<AiTraceVO> findByPage(AiTraceQueryDTO queryDto);

    void add(AiTraceAddDTO addDto);

    void update(AiTraceUpdateDTO updateDto);

    void delete(String id);

    AiTraceVO findById(Long id);

}
