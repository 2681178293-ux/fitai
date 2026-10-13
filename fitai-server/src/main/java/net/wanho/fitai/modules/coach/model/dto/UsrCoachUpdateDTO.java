package net.wanho.fitai.modules.coach.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.coach.model.po.UsrCoach;

@Data
@Schema(description = "教练修改参数")
@AutoMapper(target = UsrCoach.class)
public class UsrCoachUpdateDTO implements Serializable {

    @Schema(description = "教练ID")
    @NotNull(message = "教练ID不能为空")
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
    @Schema(description = "简介")
    private String intro;
    @Schema(description = "资质证书")
    private String certJson;
    @Schema(description = "评分(冗余)")
    private BigDecimal rating;
    @Schema(description = "学员数(冗余)")
    private Integer studentCount;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
