package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Schema(description = "SysDept查询参数")
public class SysDeptQueryDTO  {
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "状态")
    private String status;

}
