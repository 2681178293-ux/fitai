package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemPointsLog;

@Data
@Schema(description = "新增积分流水")
@AutoMapper(target = MemPointsLog.class)
public class MemPointsLogAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "规则ID")
    private Long ruleId;
    @Schema(description = "变动积分")
    @NotNull(message = "变动积分不能为空")
    private Integer changePoints;
    @Schema(description = "变动后余额")
    @NotNull(message = "变动后余额不能为空")
    private Integer balanceAfter;
    @Schema(description = "业务类型")
    @NotBlank(message = "业务类型不能为空")
    private String bizType;
    @Schema(description = "业务ID")
    private Long bizId;
    @Schema(description = "备注")
    private String remark;
}
