package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiModelAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiModelQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiModelUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiModel;
import net.wanho.fitai.modules.ai.model.vo.AiModelVO;

public interface AiModelServiceI extends MPJBaseService<AiModel> {
    IPage<AiModelVO> findByPage(AiModelQueryDTO queryDto);

    void add(AiModelAddDTO addDto);

    void update(AiModelUpdateDTO updateDto);

    void delete(String id);

    AiModelVO findById(Long id);

}
