package net.wanho.fitai.modules.mall.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "支付渠道查询参数")
public class OrdPaymentChannelQueryDTO extends PageQuery {

    @Schema(description = "渠道名称")
    private String channelName;
    @Schema(description = "编码 wechat/alipay/apple")
    private String channelCode;
    @Schema(description = "状态（0启用 1停用）")
    private String status;
}
