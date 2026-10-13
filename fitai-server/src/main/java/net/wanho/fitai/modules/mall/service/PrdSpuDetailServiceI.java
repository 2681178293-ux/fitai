package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuDetailAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuDetailQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuDetailUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdSpuDetail;
import net.wanho.fitai.modules.mall.model.vo.PrdSpuDetailVO;

public interface PrdSpuDetailServiceI extends MPJBaseService<PrdSpuDetail> {
    IPage<PrdSpuDetailVO> findByPage(PrdSpuDetailQueryDTO queryDto);

    void add(PrdSpuDetailAddDTO addDto);

    void update(PrdSpuDetailUpdateDTO updateDto);

    void delete(String id);

    PrdSpuDetailVO findById(Long id);

}
