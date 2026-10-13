package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemLevelBenefit;

@Data
@Schema(description = "新增等级权益关联")
@AutoMapper(target = MemLevelBenefit.class)
public class MemLevelBenefitAddDTO implements Serializable {

    @Schema(description = "等级ID")
    @NotNull(message = "等级ID不能为空")
    private Long levelId;
    @Schema(description = "权益ID")
    @NotNull(message = "权益ID不能为空")
    private Long benefitId;
}
