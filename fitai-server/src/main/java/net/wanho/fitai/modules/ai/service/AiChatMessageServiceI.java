package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiChatMessageAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatMessageQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatMessageUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiChatMessage;
import net.wanho.fitai.modules.ai.model.vo.AiChatMessageVO;

public interface AiChatMessageServiceI extends MPJBaseService<AiChatMessage> {
    IPage<AiChatMessageVO> findByPage(AiChatMessageQueryDTO queryDto);

    void add(AiChatMessageAddDTO addDto);

    void update(AiChatMessageUpdateDTO updateDto);

    void delete(String id);

    AiChatMessageVO findById(Long id);

}
