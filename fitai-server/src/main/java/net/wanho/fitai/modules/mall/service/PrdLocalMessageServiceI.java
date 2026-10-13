package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdLocalMessageAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdLocalMessageQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdLocalMessageUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdLocalMessage;
import net.wanho.fitai.modules.mall.model.vo.PrdLocalMessageVO;

public interface PrdLocalMessageServiceI extends MPJBaseService<PrdLocalMessage> {
    IPage<PrdLocalMessageVO> findByPage(PrdLocalMessageQueryDTO queryDto);

    void add(PrdLocalMessageAddDTO addDto);

    void update(PrdLocalMessageUpdateDTO updateDto);

    void delete(String id);

    PrdLocalMessageVO findById(String id);

}
