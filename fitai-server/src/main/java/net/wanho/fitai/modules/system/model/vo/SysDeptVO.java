package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysDept;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "SysDept视图")
@AutoMapper(target = SysDept.class)
public class SysDeptVO implements Serializable {
    @Schema(description = "部门ID")
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
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子部门")
    private List<SysDeptVO> children;

}
