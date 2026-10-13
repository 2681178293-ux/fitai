package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemberLevelBenefit;

@Data
@Schema(description = "等级权益关联表修改参数")
@AutoMapper(target = MemberLevelBenefit.class)
public class MemberLevelBenefitUpdateDTO implements Serializable {

    @Schema(description = "关联ID")
    @NotNull(message = "关联ID不能为空")
    private Long id;
    @Schema(description = "等级ID")
    private Long levelId;
    @Schema(description = "权益ID")
    private Long benefitId;
}
