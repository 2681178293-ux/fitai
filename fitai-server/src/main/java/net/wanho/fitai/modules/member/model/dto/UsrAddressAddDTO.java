package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrAddress;

@Data
@Schema(description = "新增用户收货地址")
@AutoMapper(target = UsrAddress.class)
public class UsrAddressAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "收货人")
    @NotBlank(message = "收货人不能为空")
    private String receiverName;
    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String receiverPhone;
    @Schema(description = "省")
    @NotBlank(message = "省不能为空")
    private String province;
    @Schema(description = "市")
    @NotBlank(message = "市不能为空")
    private String city;
    @Schema(description = "区")
    @NotBlank(message = "区不能为空")
    private String district;
    @Schema(description = "详细地址")
    @NotBlank(message = "详细地址不能为空")
    private String detailAddr;
    @Schema(description = "邮编")
    private String postalCode;
    @Schema(description = "默认（0否 1是）")
    private String isDefault;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
