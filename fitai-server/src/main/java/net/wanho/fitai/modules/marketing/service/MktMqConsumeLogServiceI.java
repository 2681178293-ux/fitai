package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.MktMqConsumeLogAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktMqConsumeLogQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktMqConsumeLogUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktMqConsumeLog;
import net.wanho.fitai.modules.marketing.model.vo.MktMqConsumeLogVO;

public interface MktMqConsumeLogServiceI extends MPJBaseService<MktMqConsumeLog> {
    IPage<MktMqConsumeLogVO> findByPage(MktMqConsumeLogQueryDTO queryDto);

    void add(MktMqConsumeLogAddDTO addDto);

    void update(MktMqConsumeLogUpdateDTO updateDto);

    void delete(String id);

    MktMqConsumeLogVO findById(String id);

}
