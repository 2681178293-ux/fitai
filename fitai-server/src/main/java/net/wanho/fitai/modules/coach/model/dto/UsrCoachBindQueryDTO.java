package net.wanho.fitai.modules.coach.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户教练绑定查询参数")
public class UsrCoachBindQueryDTO extends PageQuery {

    @Schema(description = "教练名(冗余)")
    private String coachName;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "绑定类型")
    private String bindType;
    @Schema(description = "绑定状态")
    private String status;
}
