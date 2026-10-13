package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemGrowthLog;

@Data
@Schema(description = "成长值流水修改参数")
@AutoMapper(target = MemGrowthLog.class)
public class MemGrowthLogUpdateDTO implements Serializable {

    @Schema(description = "流水ID")
    @NotNull(message = "流水ID不能为空")
    private Long logId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "变动成长值")
    private Integer changeValue;
    @Schema(description = "变动后成长值")
    private Integer balanceAfter;
    @Schema(description = "业务类型 sign/train/order")
    private String bizType;
    @Schema(description = "业务ID")
    private Long bizId;
    @Schema(description = "备注")
    private String remark;
}
