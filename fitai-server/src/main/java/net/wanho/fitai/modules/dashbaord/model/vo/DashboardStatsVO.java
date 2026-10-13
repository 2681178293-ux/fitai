package net.wanho.fitai.modules.dashbaord.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

// modules/dashboard/model/vo/DashboardStatsVO.java
@Data
@Schema(description = "仪表盘核心统计")
public class DashboardStatsVO {

    @Schema(description = "用户总数")
    private Long userCount;

    @Schema(description = "订单总数")
    private Long orderCount;

    @Schema(description = "销售总额（已支付等终态）")
    private BigDecimal salesAmount;

    @Schema(description = "课程总数")
    private Long courseCount;

    @Schema(description = "C 端登录统计")
    private ClientLoginStats clientLoginStats;

    @Data
    @Schema(description = "C 端登录统计")
    public static class ClientLoginStats {
        @Schema(description = "PC 路径去重用户数")
        private Long pcDistinctUsers;
        @Schema(description = "移动端路径去重用户数")
        private Long mobileDistinctUsers;
        @Schema(description = "双端均登录过的用户数")
        private Long bothEndsUsers;
        @Schema(description = "PC 路径登录总次数")
        private Long pcLoginTimes;
        @Schema(description = "移动端路径登录总次数")
        private Long mobileLoginTimes;
    }
}
