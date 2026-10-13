package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.OrdLocalMessageAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLocalMessageQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLocalMessageUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdLocalMessage;
import net.wanho.fitai.modules.mall.model.vo.OrdLocalMessageVO;

public interface OrdLocalMessageServiceI extends MPJBaseService<OrdLocalMessage> {
    IPage<OrdLocalMessageVO> findByPage(OrdLocalMessageQueryDTO queryDto);

    void add(OrdLocalMessageAddDTO addDto);

    void update(OrdLocalMessageUpdateDTO updateDto);

    void delete(String id);

    OrdLocalMessageVO findById(String id);

}
