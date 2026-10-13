package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrAddress;

@Data
@Schema(description = "用户收货地址修改参数")
@AutoMapper(target = UsrAddress.class)
public class UsrAddressUpdateDTO implements Serializable {

    @Schema(description = "地址ID")
    @NotNull(message = "地址ID不能为空")
    private Long addressId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "收货人")
    private String receiverName;
    @Schema(description = "手机号")
    private String receiverPhone;
    @Schema(description = "省")
    private String province;
    @Schema(description = "市")
    private String city;
    @Schema(description = "区")
    private String district;
    @Schema(description = "详细地址")
    private String detailAddr;
    @Schema(description = "邮编")
    private String postalCode;
    @Schema(description = "默认（0否 1是）")
    private String isDefault;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
