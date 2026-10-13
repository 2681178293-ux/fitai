package net.wanho.fitai.modules.media.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.po.MediaOssCategory;
import net.wanho.fitai.modules.media.model.vo.MediaOssCategoryVO;

import java.util.List;

public interface MediaOssCategoryServiceI extends MPJBaseService<MediaOssCategory> {
    IPage<MediaOssCategoryVO> findByPage(MediaOssCategoryQueryDTO queryDto);

    void add(MediaOssCategoryAddDTO addDto);

    void update(MediaOssCategoryUpdateDTO updateDto);

    void delete(String id);

    MediaOssCategoryVO findById(Long id);

    List<MediaOssCategoryVO> tree();

    void move(MediaOssCategoryMoveDTO moveDto);

    void updateCategoryName(MediaOssCategoryUpdateNameDTO moveDto);

    void addCategory(MediaOssCategorySimpleAddDTO addDto);

    void deleteCategory(String categoryId);
}
