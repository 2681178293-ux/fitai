package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemberBenefit;

@Data
@Schema(description = "会员权益表修改参数")
@AutoMapper(target = MemberBenefit.class)
public class MemberBenefitUpdateDTO implements Serializable {

    @Schema(description = "权益ID")
    @NotNull(message = "权益ID不能为空")
    private Long id;
    @Schema(description = "权益代码")
    private String code;
    @Schema(description = "权益名称")
    private String name;
    @Schema(description = "权益描述")
    private String description;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "最低会员等级")
    private Integer minLevel;
    @Schema(description = "权益值")
    private String value;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "状态：0-停用 1-启用")
    private Integer status;
    @Schema(description = "删除标志：0-正常 1-删除")
    private Integer deleted;
}
