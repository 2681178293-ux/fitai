package net.wanho.fitai.modules.media.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.po.MediaVodCategory;
import net.wanho.fitai.modules.media.model.vo.MediaVodCategoryVO;

import java.util.List;

public interface MediaVodCategoryServiceI extends MPJBaseService<MediaVodCategory> {
    IPage<MediaVodCategoryVO> findByPage(MediaVodCategoryQueryDTO queryDto);

    void add(MediaVodCategoryAddDTO addDto);

    void update(MediaVodCategoryUpdateDTO updateDto);

    void delete(String id);

    MediaVodCategoryVO findById(Long id);

    List<MediaVodCategoryVO> getCategoryTree();

    void moveCategory(MediaVodCategoryMoveDTO moveDto);

    void addCategory(MediaVodCategorySimpleAddDTO addDto);

    void deleteCategory(String categoryId);
}
