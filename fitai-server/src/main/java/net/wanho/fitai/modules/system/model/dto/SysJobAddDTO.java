package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysJob;
import java.io.Serializable;

@Data
@Schema(description = "新增SysJob参数")
@AutoMapper(target = SysJob.class)
public class SysJobAddDTO implements Serializable {
    @Schema(description = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String jobName;
    @Schema(description = "任务组名")
    @NotBlank(message = "任务组名不能为空")
    private String jobGroup;
    @Schema(description = "调用目标")
    @NotBlank(message = "调用目标不能为空")
    private String invokeTarget;
    @Schema(description = "cron表达式")
    @NotBlank(message = "cron表达式不能为空")
    private String cronExpression;
    @Schema(description = "计划策略")
    private String misfirePolicy;
    @Schema(description = "并发")
    private String concurrent;
    @Schema(description = "状态")
    @NotBlank(message = "状态不能为空")
    private String status;
    @Schema(description = "备注")
    private String remark;

}
