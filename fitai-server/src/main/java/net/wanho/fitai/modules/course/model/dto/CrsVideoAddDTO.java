package net.wanho.fitai.modules.course.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.course.model.po.CrsVideo;

@Data
@Schema(description = "新增课程视频")
@AutoMapper(target = CrsVideo.class)
public class CrsVideoAddDTO implements Serializable {

    @Schema(description = "课程ID")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    @Schema(description = "课程名(冗余)")
    private String courseTitle;
    @Schema(description = "视频标题")
    @NotBlank(message = "视频标题不能为空")
    private String videoTitle;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "VOD视频ID(media_vod)")
    private Long vodAssetId;
    @Schema(description = "VOD分类ID(冗余)")
    private Long vodCategoryId;
    @Schema(description = "VOD分类名(冗余)")
    private String vodCategoryName;
    @Schema(description = "封面OSS图片ID")
    private Long coverOssId;
    @Schema(description = "封面URL(冗余,来自media_oss)")
    private String coverUrl;
    @Schema(description = "VOD VideoId(冗余)")
    private String vodVideoId;
    @Schema(description = "VOD封面(冗余)")
    private String vodCoverUrl;
    @Schema(description = "播放地址(冗余)")
    private String vodPlayUrl;
    @Schema(description = "时长秒(冗余)")
    private BigDecimal vodDuration;
    @Schema(description = "VOD状态(冗余)")
    private String vodStatus;
    @Schema(description = "试看（0否 1是）")
    private String isPreview;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
