package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemPackage;

@Data
@Schema(description = "会员套餐修改参数")
@AutoMapper(target = MemPackage.class)
public class MemPackageUpdateDTO implements Serializable {

    @Schema(description = "套餐ID")
    @NotNull(message = "套餐ID不能为空")
    private Long packageId;
    @Schema(description = "套餐名称")
    private String packageName;
    @Schema(description = "类型（1月卡 2季卡 3年卡）")
    private String packageType;
    @Schema(description = "有效天数")
    private Integer durationDays;
    @Schema(description = "售价")
    private BigDecimal price;
    @Schema(description = "原价")
    private BigDecimal origPrice;
    @Schema(description = "赠送等级ID")
    private Long levelId;
    @Schema(description = "等级名(冗余)")
    private String levelName;
    @Schema(description = "AI月Token额度")
    private Integer aiQuotaMonthly;
    @Schema(description = "AI日Token上限（会员）")
    private Integer aiQuotaDailyTokens;
    @Schema(description = "AI日对话次数上限（会员）")
    private Integer aiDailyChatLimit;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "状态（0上架 1下架）")
    private String status;
}
