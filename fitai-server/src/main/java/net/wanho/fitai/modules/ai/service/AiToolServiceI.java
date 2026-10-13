package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiToolAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiToolQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiToolUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiTool;
import net.wanho.fitai.modules.ai.model.vo.AiToolVO;

public interface AiToolServiceI extends MPJBaseService<AiTool> {
    IPage<AiToolVO> findByPage(AiToolQueryDTO queryDto);

    void add(AiToolAddDTO addDto);

    void update(AiToolUpdateDTO updateDto);

    void delete(String id);

    AiToolVO findById(Long id);

}
