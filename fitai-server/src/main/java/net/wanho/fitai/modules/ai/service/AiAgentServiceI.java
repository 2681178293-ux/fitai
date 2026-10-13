package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiAgentAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiAgentQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiAgentUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiAgent;
import net.wanho.fitai.modules.ai.model.vo.AiAgentVO;

public interface AiAgentServiceI extends MPJBaseService<AiAgent> {
    IPage<AiAgentVO> findByPage(AiAgentQueryDTO queryDto);

    void add(AiAgentAddDTO addDto);

    void update(AiAgentUpdateDTO updateDto);

    void delete(String id);

    AiAgentVO findById(Long id);

}
