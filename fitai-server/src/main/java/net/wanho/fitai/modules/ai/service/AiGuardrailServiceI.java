package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiGuardrailAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiGuardrailQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiGuardrailUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiGuardrail;
import net.wanho.fitai.modules.ai.model.vo.AiGuardrailVO;

public interface AiGuardrailServiceI extends MPJBaseService<AiGuardrail> {
    IPage<AiGuardrailVO> findByPage(AiGuardrailQueryDTO queryDto);

    void add(AiGuardrailAddDTO addDto);

    void update(AiGuardrailUpdateDTO updateDto);

    void delete(String id);

    AiGuardrailVO findById(Long id);

}
