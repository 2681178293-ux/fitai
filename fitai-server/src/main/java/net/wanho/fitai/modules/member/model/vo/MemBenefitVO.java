package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.member.model.po.MemBenefit;

import java.io.Serializable;

@Data
@Schema(description = "会员权益视图")
@AutoMapper(target = MemBenefit.class)
public class MemBenefitVO implements Serializable {

    @Schema(description = "权益ID")
    private Long benefitId;
    @Schema(description = "图标")
    private String iconUrl;
    @Schema(description = "权益名称")
    private String benefitName;
    @Schema(description = "最低等级")
    private Integer minLevel;
    @Schema(description = "权益编码")
    private String benefitCode;
    @Schema(description = "权益值")
    private String benefitValue;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
