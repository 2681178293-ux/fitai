package net.wanho.fitai.modules.media.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.media.model.po.MediaOss;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@Schema(description = "新增VOD视频")
@AutoMapper(target = MediaOss.class)
public class MediaVodUploadDTO implements Serializable {


    @Schema(description = "VOD分类ID")
    private Long categoryId;

    @Schema(description = "上传文件")
    private MultipartFile file;
}
