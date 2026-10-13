package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrAddress;

@Data
@Schema(description = "用户收货地址视图")
@AutoMapper(target = UsrAddress.class)
public class UsrAddressVO implements Serializable {

    @Schema(description = "地址ID")
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
