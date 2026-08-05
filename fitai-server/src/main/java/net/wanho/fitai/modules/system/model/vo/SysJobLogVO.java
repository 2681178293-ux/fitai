package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysJobLog;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Schema(description = "SysJobLog视图")
@AutoMapper(target = SysJobLog.class)
public class SysJobLogVO implements Serializable {
    @Schema(description = "日志ID")
    private Long jobLogId;
    @Schema(description = "任务ID")
    private Long jobId;
    @Schema(description = "任务名称")
    private String jobName;
    @Schema(description = "任务组名")
    private String jobGroup;
    @Schema(description = "调用目标")
    private String invokeTarget;
    @Schema(description = "日志信息")
    private String jobMessage;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "异常信息")
    private String exceptionInfo;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
