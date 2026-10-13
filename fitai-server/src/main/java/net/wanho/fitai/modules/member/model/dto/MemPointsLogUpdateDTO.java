package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemPointsLog;

@Data
@Schema(description = "积分流水修改参数")
@AutoMapper(target = MemPointsLog.class)
public class MemPointsLogUpdateDTO implements Serializable {

    @Schema(description = "流水ID")
    @NotNull(message = "流水ID不能为空")
    private Long logId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "规则ID")
    private Long ruleId;
    @Schema(description = "变动积分")
    private Integer changePoints;
    @Schema(description = "变动后余额")
    private Integer balanceAfter;
    @Schema(description = "业务类型")
    private String bizType;
    @Schema(description = "业务ID")
    private Long bizId;
    @Schema(description = "备注")
    private String remark;
}
