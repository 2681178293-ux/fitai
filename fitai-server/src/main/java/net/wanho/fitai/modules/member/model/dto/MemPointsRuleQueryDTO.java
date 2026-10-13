package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "积分规则查询参数")
public class MemPointsRuleQueryDTO extends PageQuery {

    @Schema(description = "规则名称")
    private String ruleName;
    @Schema(description = "时间类型")
    private String eventType;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
