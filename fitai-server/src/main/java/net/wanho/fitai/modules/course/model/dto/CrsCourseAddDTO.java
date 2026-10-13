package net.wanho.fitai.modules.course.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.course.model.po.CrsCourse;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "新增课程")
@AutoMapper(target = CrsCourse.class)
public class CrsCourseAddDTO implements Serializable {

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    @Schema(description = "分类名(冗余)")
    private String categoryName;
    @Schema(description = "课程标题")
    @NotBlank(message = "课程标题不能为空")
    private String courseTitle;
    @Schema(description = "封面OSS图片ID")
    private Long coverOssId;
    @Schema(description = "封面URL(冗余,来自media_oss)")
    private String coverUrl;
    @Schema(description = "教练ID")
    private Long coachId;
    @Schema(description = "教练名(冗余)")
    private String coachName;
    @Schema(description = "难度（1初级 2中级 3高级）")
    private String difficulty;
    @Schema(description = "总时长分钟(冗余)")
    private Integer durationMin;
    @Schema(description = "课时数(冗余)")
    private Integer lessonCount;
    @Schema(description = "价格")
    private BigDecimal price;
    @Schema(description = "是否免费（0否 1是）")
    private String isFree;
    @Schema(description = "简介")
    private String summary;
    @Schema(description = "播放量(冗余)")
    private Integer viewCount;
    @Schema(description = "点赞数(冗余)")
    private Integer likeCount;
    @Schema(description = "状态（0上架 1下架 2草稿）")
    private String status;
    @Schema(description = "视频ID(冗余)")
    private Long videoId;
    @Schema(description = "卡路里(冗余)")
    private Integer calories;
    @Schema(description = "所需器材(冗余)")
    private String equipment;
}
