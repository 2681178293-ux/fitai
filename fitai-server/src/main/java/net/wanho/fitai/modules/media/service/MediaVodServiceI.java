package net.wanho.fitai.modules.media.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.po.MediaVod;
import net.wanho.fitai.modules.media.model.vo.MediaVodVO;

public interface MediaVodServiceI extends MPJBaseService<MediaVod> {
    IPage<MediaVodVO> findByPage(MediaVodQueryDTO queryDto);

    void add(MediaVodAddDTO addDto);

    void update(MediaVodUpdateDTO updateDto);

    void delete(String id);

    MediaVodVO findById(Long id);

    MediaVodVO upload(MediaVodUploadDTO uploadDto);

    IPage<MediaVodVO> findByPage(MediaVodQuerySimpleDTO queryDto);

    String getPlayUrl(Long id);

    void delete(MediaVodDeleteDTO deleteDTO);
}
