package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysOperLog查询参数")
public class SysOperLogQueryDTO extends PageQuery {
    @Schema(description = "模块标题")
    private String title;
    @Schema(description = "操作人员")
    private String operName;
    @Schema(description = "业务类型")
    private Integer businessType;
    @Schema(description = "状态")
    private String status;

}
