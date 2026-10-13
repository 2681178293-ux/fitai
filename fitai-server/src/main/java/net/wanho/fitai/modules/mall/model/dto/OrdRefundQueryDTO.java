package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "售后退款查询参数")
public class OrdRefundQueryDTO extends PageQuery {

    @Schema(description = "退款单号")
    private String refundNo;
    @Schema(description = "原订单号(冗余)")
    private String orderNo;
    @Schema(description = "状态（0待审核 1已同意 2已拒绝 3已退款）")
    private String status;
}
