package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysDept;
import java.io.Serializable;

@Data
@Schema(description = "修改SysDept参数")
@AutoMapper(target = SysDept.class)
public class SysDeptUpdateDTO implements Serializable {
    @Schema(description = "deptId")
    @NotNull(message = "deptId不能为空")
    private Long deptId;
    @Schema(description = "父部门ID")
    private Long parentId;
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "显示顺序")
    private Integer orderNum;
    @Schema(description = "负责人")
    private String leader;
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "状态")
    private String status;

}
