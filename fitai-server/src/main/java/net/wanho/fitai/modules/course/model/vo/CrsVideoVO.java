package net.wanho.fitai.modules.course.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.course.model.po.CrsVideo;

@Data
@Schema(description = "课程视频视图")
@AutoMapper(target = CrsVideo.class)
public class CrsVideoVO implements Serializable {

    @Schema(description = "视频课时ID")
    private Long videoId;
    @Schema(description = "课程ID")
    private Long courseId;
    @Schema(description = "课程名(冗余)")
    private String courseTitle;
    @Schema(description = "视频标题")
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
