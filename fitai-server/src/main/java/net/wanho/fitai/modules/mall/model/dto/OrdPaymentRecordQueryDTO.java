package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "支付流水查询参数")
public class OrdPaymentRecordQueryDTO extends PageQuery {

    @Schema(description = "支付流水号")
    private String paymentNo;
    @Schema(description = "合并支付批次号")
    private String mergeNo;
    @Schema(description = "业务订单号(冗余)")
    private String orderNo;
    @Schema(description = "支付渠道 wechat/alipay")
    private String channelCode;
}
