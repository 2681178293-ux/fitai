package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiUserQuotaAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiUserQuotaQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiUserQuotaUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiUserQuota;
import net.wanho.fitai.modules.ai.model.vo.AiUserQuotaVO;

public interface AiUserQuotaServiceI extends MPJBaseService<AiUserQuota> {
    IPage<AiUserQuotaVO> findByPage(AiUserQuotaQueryDTO queryDto);

    void add(AiUserQuotaAddDTO addDto);

    void update(AiUserQuotaUpdateDTO updateDto);

    void delete(String id);

    AiUserQuotaVO findById(Long id);

}
