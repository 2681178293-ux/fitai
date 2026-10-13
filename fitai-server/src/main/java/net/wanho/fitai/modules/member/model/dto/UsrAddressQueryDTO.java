package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户收货地址查询参数")
public class UsrAddressQueryDTO extends PageQuery {

    @Schema(description = "收货人")
    private String receiverName;
    @Schema(description = "手机号")
    private String receiverPhone;
    @Schema(description = "邮编")
    private String postalCode;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
