package net.wanho.fitai.modules.course.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.course.model.po.CrsLearnProgress;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "课程学习进度视图")
@AutoMapper(target = CrsLearnProgress.class)
public class CrsLearnProgressVO implements Serializable {

    @Schema(description = "进度ID")
    private Long progressId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "课程ID")
    private Long courseId;
    @Schema(description = "课程名(冗余)")
    private String courseTitle;
    @Schema(description = "当前课时ID")
    private Long videoId;
    @Schema(description = "课时名(冗余)")
    private String videoTitle;
    @Schema(description = "进度%")
    private Integer progressPct;
    @Schema(description = "已学秒数")
    private Integer learnedSec;
    @Schema(description = "总秒数(冗余)")
    private Integer totalSec;
    @Schema(description = "完课（0否 1是）")
    private String isFinished;
    @Schema(description = "最后学习时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLearnTime;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "用户昵称")
    private String nickName;
    /** 联表：学员手机号 */
    private String mobile;
    /** 联表：学员头像 */
    private String avatar;
    /** 联表：会员等级名称 */
    private String memberLevelName;

}
