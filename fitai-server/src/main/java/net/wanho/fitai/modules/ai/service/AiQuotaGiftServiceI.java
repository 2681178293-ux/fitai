package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiQuotaGiftAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiQuotaGiftQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiQuotaGiftUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiQuotaGift;
import net.wanho.fitai.modules.ai.model.vo.AiQuotaGiftVO;

public interface AiQuotaGiftServiceI extends MPJBaseService<AiQuotaGift> {
    IPage<AiQuotaGiftVO> findByPage(AiQuotaGiftQueryDTO queryDto);

    void add(AiQuotaGiftAddDTO addDto);

    void update(AiQuotaGiftUpdateDTO updateDto);

    void delete(String id);

    AiQuotaGiftVO findById(Long id);

}
