package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.member.model.po.MemBenefit;

import java.io.Serializable;

@Data
@Schema(description = "新增会员权益")
@AutoMapper(target = MemBenefit.class)
public class MemBenefitAddDTO implements Serializable {

    @Schema(description = "权益名称")
    @NotBlank(message = "权益名称不能为空")
    private String benefitName;
    @Schema(description = "权益编码")
    @NotBlank(message = "权益编码不能为空")
    private String benefitCode;
    @Schema(description = "图标URL")
    private String iconUrl;
    @Schema(description = "最低会员等级")
    private Integer minLevel;
    @Schema(description = "权益值")
    private String benefitValue;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
