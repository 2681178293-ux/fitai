package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdMqConsumeLogAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdMqConsumeLogQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdMqConsumeLogUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdMqConsumeLog;
import net.wanho.fitai.modules.mall.model.vo.PrdMqConsumeLogVO;

public interface PrdMqConsumeLogServiceI extends MPJBaseService<PrdMqConsumeLog> {
    IPage<PrdMqConsumeLogVO> findByPage(PrdMqConsumeLogQueryDTO queryDto);

    void add(PrdMqConsumeLogAddDTO addDto);

    void update(PrdMqConsumeLogUpdateDTO updateDto);

    void delete(String id);

    PrdMqConsumeLogVO findById(String id);

}
