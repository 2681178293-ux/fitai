package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemLevelBenefit;

@Data
@Schema(description = "等级权益关联视图")
@AutoMapper(target = MemLevelBenefit.class)
public class MemLevelBenefitVO implements Serializable {

    @Schema(description = "关联ID")
    private Long levelBenefitId;
    @Schema(description = "等级ID")
    private Long levelId;
    @Schema(description = "权益ID")
    private Long benefitId;
}
