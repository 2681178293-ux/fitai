package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysLoginLog查询参数")
public class SysLoginLogQueryDTO extends PageQuery {
    @Schema(description = "登录账号")
    private String userName;
    @Schema(description = "用户类型")
    private String userType;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "客户端")
    private String clientType;

}
