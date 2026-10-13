package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiVectorIndexAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiVectorIndexQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiVectorIndexUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiVectorIndex;
import net.wanho.fitai.modules.ai.model.vo.AiVectorIndexVO;

public interface AiVectorIndexServiceI extends MPJBaseService<AiVectorIndex> {
    IPage<AiVectorIndexVO> findByPage(AiVectorIndexQueryDTO queryDto);

    void add(AiVectorIndexAddDTO addDto);

    void update(AiVectorIndexUpdateDTO updateDto);

    void delete(String id);

    AiVectorIndexVO findById(Long id);

}
