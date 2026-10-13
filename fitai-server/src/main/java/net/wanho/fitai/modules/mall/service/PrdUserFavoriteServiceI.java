package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.PrdUserFavoriteAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdUserFavoriteQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdUserFavoriteUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdUserFavorite;
import net.wanho.fitai.modules.mall.model.vo.PrdUserFavoriteVO;

public interface PrdUserFavoriteServiceI extends MPJBaseService<PrdUserFavorite> {
    IPage<PrdUserFavoriteVO> findByPage(PrdUserFavoriteQueryDTO queryDto);

    void add(PrdUserFavoriteAddDTO addDto);

    void update(PrdUserFavoriteUpdateDTO updateDto);

    void delete(String id);

    PrdUserFavoriteVO findById(Long id);

}
