package net.wanho.fitai.modules.ai.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetRowAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetRowQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetRowUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiDatasetRow;
import net.wanho.fitai.modules.ai.model.vo.AiDatasetRowVO;

public interface AiDatasetRowServiceI extends MPJBaseService<AiDatasetRow> {
    IPage<AiDatasetRowVO> findByPage(AiDatasetRowQueryDTO queryDto);

    void add(AiDatasetRowAddDTO addDto);

    void update(AiDatasetRowUpdateDTO updateDto);

    void delete(String id);

    AiDatasetRowVO findById(Long id);

}
