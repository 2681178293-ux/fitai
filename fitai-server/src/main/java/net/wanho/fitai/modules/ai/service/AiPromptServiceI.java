package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiPromptAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPromptQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPromptUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiPrompt;
import net.wanho.fitai.modules.ai.model.vo.AiPromptVO;

public interface AiPromptServiceI extends MPJBaseService<AiPrompt> {
    IPage<AiPromptVO> findByPage(AiPromptQueryDTO queryDto);

    void add(AiPromptAddDTO addDto);

    void update(AiPromptUpdateDTO updateDto);

    void delete(String id);

    AiPromptVO findById(Long id);

}
