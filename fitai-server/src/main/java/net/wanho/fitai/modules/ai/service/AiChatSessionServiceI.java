package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiChatSessionAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatSessionQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatSessionUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiChatSession;
import net.wanho.fitai.modules.ai.model.vo.AiChatSessionVO;

public interface AiChatSessionServiceI extends MPJBaseService<AiChatSession> {
    IPage<AiChatSessionVO> findByPage(AiChatSessionQueryDTO queryDto);

    void add(AiChatSessionAddDTO addDto);

    void update(AiChatSessionUpdateDTO updateDto);

    void delete(String id);

    AiChatSessionVO findById(Long id);

}
