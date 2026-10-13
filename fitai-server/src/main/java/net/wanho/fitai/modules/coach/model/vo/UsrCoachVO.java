package net.wanho.fitai.modules.coach.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.coach.model.po.UsrCoach;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "教练视图")
@AutoMapper(target = UsrCoach.class)
public class UsrCoachVO implements Serializable {

    @Schema(description = "教练ID")
    private Long coachId;
    @Schema(description = "姓名")
    private String coachName;
    @Schema(description = "头衔")
    private String title;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "手机号")
    private String mobile;
    @Schema(description = "专长标签")
    private String specialty;
    @Schema(description = "评分(冗余)")
    private BigDecimal rating;
    @Schema(description = "学员数(冗余)")
    private Integer studentCount;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "课时费")
    private BigDecimal hourlyFee;
    @Schema(description = "性别")
    private String sex;
}
