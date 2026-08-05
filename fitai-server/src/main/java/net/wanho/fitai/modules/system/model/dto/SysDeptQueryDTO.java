package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysDept查询参数")
public class SysDeptQueryDTO extends PageQuery {
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "状态")
    private String status;

}
