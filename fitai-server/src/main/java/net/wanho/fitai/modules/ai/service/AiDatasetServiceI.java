package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiDataset;
import net.wanho.fitai.modules.ai.model.vo.AiDatasetVO;

public interface AiDatasetServiceI extends MPJBaseService<AiDataset> {
    IPage<AiDatasetVO> findByPage(AiDatasetQueryDTO queryDto);

    void add(AiDatasetAddDTO addDto);

    void update(AiDatasetUpdateDTO updateDto);

    void delete(String id);

    AiDatasetVO findById(Long id);

}
