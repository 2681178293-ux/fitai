package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysJob;
import java.io.Serializable;

@Data
@Schema(description = "SysJob视图")
@AutoMapper(target = SysJob.class)
public class SysJobVO implements Serializable {
    @Schema(description = "任务ID")
    private Long jobId;
    @Schema(description = "任务名称")
    private String jobName;
    @Schema(description = "任务组名")
    private String jobGroup;
    @Schema(description = "调用目标")
    private String invokeTarget;
    @Schema(description = "cron表达式")
    private String cronExpression;
    @Schema(description = "计划策略")
    private String misfirePolicy;
    @Schema(description = "并发")
    private String concurrent;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "备注")
    private String remark;

}
