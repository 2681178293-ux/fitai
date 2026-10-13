package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "物流查询参数")
public class OrdLogisticsQueryDTO extends PageQuery {

    @Schema(description = "订单号(冗余)")
    private String orderNo;
    @Schema(description = "快递公司编码")
    private String expressCode;
    @Schema(description = "快递公司(冗余)")
    private String expressName;
    @Schema(description = "运单号")
    private String trackingNo;
    @Schema(description = "状态（0待发货 1已发货 2运输中 3已签收）")
    private String status;
}
