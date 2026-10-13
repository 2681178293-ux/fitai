package net.wanho.fitai.modules.media.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.media.model.po.MediaVod;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "VOD视频修改参数")
@AutoMapper(target = MediaVod.class)
public class MediaVodUpdateDTO implements Serializable {


    @Schema(description = "VOD分类ID")
    private Long categoryId;
    @Schema(description = "分类名(冗余)")
    private String categoryName;
    @Schema(description = "业务模块 course/action")
    private String bizModule;
    @Schema(description = "业务ID")
    private Long bizId;
    @Schema(description = "阿里云VOD VideoId")
    private String vodVideoId;
    @Schema(description = "封面URL(自有OSS永久地址,由VOD截图转存)")
    private String vodCoverUrl;
    @Schema(description = "时长(秒)")
    private BigDecimal vodDuration;
    @Schema(description = "文件大小(字节)")
    private Long vodSize;
    @Schema(description = "VOD状态 Normal/Uploading/Transcoding/TranscodeFail")
    private String vodStatus;
    @Schema(description = "阿里云VOD分类ID(冗余)")
    private Long vodCateId;
    @Schema(description = "存储地址如out-in-xx")
    private String vodStorageLocation;
    @Schema(description = "截图URL列表")
    private String vodSnapshotUrls;
    @Schema(description = "状态（0正常 1处理中 2失败）")
    private String status;
}
