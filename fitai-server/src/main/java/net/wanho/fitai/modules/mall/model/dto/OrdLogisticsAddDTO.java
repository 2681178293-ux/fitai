package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.OrdLogistics;

@Data
@Schema(description = "新增物流")
@AutoMapper(target = OrdLogistics.class)
public class OrdLogisticsAddDTO implements Serializable {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    @Schema(description = "订单号(冗余)")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @Schema(description = "快递公司编码")
    @NotBlank(message = "快递公司编码不能为空")
    private String expressCode;
    @Schema(description = "快递公司(冗余)")
    private String expressName;
    @Schema(description = "运单号")
    @NotBlank(message = "运单号不能为空")
    private String trackingNo;
    @Schema(description = "发货时间")
    private LocalDateTime shipTime;
    @Schema(description = "状态（0待发货 1已发货 2运输中 3已签收）")
    private String status;
}
