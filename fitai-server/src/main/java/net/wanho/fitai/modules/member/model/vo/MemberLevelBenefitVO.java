package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemberLevelBenefit;

@Data
@Schema(description = "等级权益关联表视图")
@AutoMapper(target = MemberLevelBenefit.class)
public class MemberLevelBenefitVO implements Serializable {

    @Schema(description = "关联ID")
    private Long id;
    @Schema(description = "等级ID")
    private Long levelId;
    @Schema(description = "权益ID")
    private Long benefitId;
}
