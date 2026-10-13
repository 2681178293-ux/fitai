package net.wanho.fitai.modules.coach.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.coach.model.po.UsrCoach;

@Data
@Schema(description = "新增教练")
@AutoMapper(target = UsrCoach.class)
public class UsrCoachAddDTO implements Serializable {

    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
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
