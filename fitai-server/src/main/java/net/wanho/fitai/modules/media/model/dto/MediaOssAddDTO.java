package net.wanho.fitai.modules.media.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.media.model.po.MediaOss;

import java.io.Serializable;

@Data
@Schema(description = "新增OSS图片")
@AutoMapper(target = MediaOss.class)
public class MediaOssAddDTO implements Serializable {

    @Schema(description = "图片名称")
    @NotBlank(message = "图片名称不能为空")
    private String ossName;
    @Schema(description = "OSS分类ID")
    private Long categoryId;
    @Schema(description = "分类名(冗余)")
    private String categoryName;
    @Schema(description = "业务模块 course/product/marketing/action")
    @NotBlank(message = "业务模块 course不能为空")
    private String bizModule;
    @Schema(description = "业务ID")
    private Long bizId;
    @Schema(description = "OSS Bucket")
    @NotBlank(message = "OSS不能为空")
    private String ossBucket;
    @Schema(description = "OSS ObjectKey")
    @NotBlank(message = "OSS不能为空")
    private String ossObjectKey;
    @Schema(description = "OSS访问URL")
    @NotBlank(message = "OSS访问URL不能为空")
    private String ossUrl;
    @Schema(description = "原始文件名")
    private String ossFileName;
    @Schema(description = "文件大小(字节)")
    private Long ossFileSize;
    @Schema(description = "OSS ETag")
    private String ossEtag;
    @Schema(description = "MIME类型")
    private String mimeType;
    @Schema(description = "宽px")
    private Integer width;
    @Schema(description = "高px")
    private Integer height;
    @Schema(description = "状态（0正常 1失败）")
    private String status;
}
