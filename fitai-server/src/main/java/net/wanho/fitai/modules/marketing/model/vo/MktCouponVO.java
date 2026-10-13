package net.wanho.fitai.modules.marketing.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktCoupon;

@Data
@Schema(description = "优惠券视图")
@AutoMapper(target = MktCoupon.class)
public class MktCouponVO implements Serializable {

    @Schema(description = "优惠券ID")
    private Long id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "类型：1满减 2折扣 3无门槛")
    private Integer type;
    @Schema(description = "优惠值（金额或折扣）")
    private BigDecimal value;
    @Schema(description = "最低消费")
    private BigDecimal minAmount;
    @Schema(description = "最大优惠（折扣券封顶）")
    private BigDecimal maxDiscount;
    @Schema(description = "发放总量")
    private Integer totalCount;
    @Schema(description = "剩余数量")
    private Integer remainCount;
    @Schema(description = "每人限领")
    private Integer limitPerUser;
    @Schema(description = "生效时间")
    private LocalDateTime startTime;
    @Schema(description = "失效时间")
    private LocalDateTime endTime;
    @Schema(description = "使用范围：all/category/product")
    private String useScope;
    @Schema(description = "范围ID列表")
    private String scopeIds;
    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}
