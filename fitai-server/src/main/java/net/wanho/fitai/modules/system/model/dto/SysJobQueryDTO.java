package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysJob查询参数")
public class SysJobQueryDTO extends PageQuery {
    @Schema(description = "任务名称")
    private String jobName;
    @Schema(description = "任务组名")
    private String jobGroup;
    @Schema(description = "状态")
    private String status;

}
