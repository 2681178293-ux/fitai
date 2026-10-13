package net.wanho.fitai.modules.media.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.po.MediaOss;
import net.wanho.fitai.modules.media.model.vo.MediaOssVO;

public interface MediaOssServiceI extends MPJBaseService<MediaOss> {
    IPage<MediaOssVO> findByPage(MediaOssQueryDTO queryDto);

    void add(MediaOssAddDTO addDto);

    void update(MediaOssUpdateDTO updateDto);

    void delete(String id);

    MediaOssVO findById(Long id);

    MediaOssVO upload( MediaOssUploadDTO uploadDto);

    IPage<MediaOssVO> findByPage(MediaOssQuerySimpleDTO queryDto);

    void delete(MediaOssDeleteDTO deleteDTO);
}
