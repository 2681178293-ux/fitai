package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuImageAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuImageQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuImageUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdSpuImage;
import net.wanho.fitai.modules.mall.model.vo.PrdSpuImageVO;

public interface PrdSpuImageServiceI extends MPJBaseService<PrdSpuImage> {
    IPage<PrdSpuImageVO> findByPage(PrdSpuImageQueryDTO queryDto);

    void add(PrdSpuImageAddDTO addDto);

    void update(PrdSpuImageUpdateDTO updateDto);

    void delete(String id);

    PrdSpuImageVO findById(Long id);

}
