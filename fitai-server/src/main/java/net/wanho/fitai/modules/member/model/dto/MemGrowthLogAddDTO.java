package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemGrowthLog;

@Data
@Schema(description = "新增成长值流水")
@AutoMapper(target = MemGrowthLog.class)
public class MemGrowthLogAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "变动成长值")
    @NotNull(message = "变动成长值不能为空")
    private Integer changeValue;
    @Schema(description = "变动后成长值")
    @NotNull(message = "变动后成长值不能为空")
    private Integer balanceAfter;
    @Schema(description = "业务类型 sign/train/order")
    @NotBlank(message = "业务类型 sign不能为空")
    private String bizType;
    @Schema(description = "业务ID")
    private Long bizId;
    @Schema(description = "备注")
    private String remark;
}
