package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdPaymentChannel;

@Data
@Schema(description = "支付渠道修改参数")
@AutoMapper(target = OrdPaymentChannel.class)
public class OrdPaymentChannelUpdateDTO implements Serializable {

    @Schema(description = "渠道ID")
    @NotNull(message = "渠道ID不能为空")
    private Long channelId;
    @Schema(description = "渠道名称")
    private String channelName;
    @Schema(description = "编码 wechat/alipay/apple")
    private String channelCode;
    @Schema(description = "商户号")
    private String merchantId;
    @Schema(description = "配置(密钥引用)")
    private String configJson;
    @Schema(description = "费率")
    private BigDecimal feeRate;
    @Schema(description = "状态（0启用 1停用）")
    private String status;
}
