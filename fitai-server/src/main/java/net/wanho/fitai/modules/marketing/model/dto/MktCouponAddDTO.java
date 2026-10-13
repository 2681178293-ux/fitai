package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktCoupon;

@Data
@Schema(description = "新增优惠券")
@AutoMapper(target = MktCoupon.class)
public class MktCouponAddDTO implements Serializable {

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;
    @Schema(description = "类型：1满减 2折扣 3无门槛")
    @NotNull(message = "类型不能为空")
    private Integer type;
    @Schema(description = "优惠值（金额或折扣）")
    @NotNull(message = "优惠值不能为空")
    private BigDecimal value;
    @Schema(description = "最低消费")
    private BigDecimal minAmount;
    @Schema(description = "最大优惠（折扣券封顶）")
    private BigDecimal maxDiscount;
    @Schema(description = "发放总量")
    @NotNull(message = "发放总量不能为空")
    private Integer totalCount;
    @Schema(description = "剩余数量")
    @NotNull(message = "剩余数量不能为空")
    private Integer remainCount;
    @Schema(description = "每人限领")
    private Integer limitPerUser;
    @Schema(description = "生效时间")
    @NotNull(message = "生效时间不能为空")
    private LocalDateTime startTime;
    @Schema(description = "失效时间")
    @NotNull(message = "失效时间不能为空")
    private LocalDateTime endTime;
    @Schema(description = "使用范围：all/category/product")
    private String useScope;
    @Schema(description = "范围ID列表")
    private String scopeIds;
    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}
