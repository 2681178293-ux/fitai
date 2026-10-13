package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.member.model.po.MemberPlan;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "会员套餐表修改参数")
@AutoMapper(target = MemberPlan.class)
public class MemberPlanUpdateDTO implements Serializable {

    @Schema(description = "套餐ID")
    @NotNull(message = "套餐ID不能为空")
    private Long id;
    @Schema(description = "套餐名称")
    private String name;
    @Schema(description = "套餐类型：1-月卡 2-季卡 3-年卡")
    private String type;
    @Schema(description = "有效天数")
    private Integer durationDays;
    @Schema(description = "价格")
    private BigDecimal price;
    @Schema(description = "原价")
    private BigDecimal originalPrice;
    @Schema(description = "套餐描述")
    private String description;
    @Schema(description = "权益列表")
    private String benefits;
    @Schema(description = "每月加赠AI Token")
    private Integer aiTokenQuotaMonthly;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "状态：0-上架 1-下架")
    private String status;
}
