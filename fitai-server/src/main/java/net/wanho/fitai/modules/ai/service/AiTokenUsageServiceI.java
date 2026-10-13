package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiTokenUsageAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTokenUsageQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTokenUsageUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiTokenUsage;
import net.wanho.fitai.modules.ai.model.vo.AiTokenUsageVO;

public interface AiTokenUsageServiceI extends MPJBaseService<AiTokenUsage> {
    IPage<AiTokenUsageVO> findByPage(AiTokenUsageQueryDTO queryDto);

    void add(AiTokenUsageAddDTO addDto);

    void update(AiTokenUsageUpdateDTO updateDto);

    void delete(String id);

    AiTokenUsageVO findById(Long id);

}
