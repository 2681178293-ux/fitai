package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemberBenefit;

@Data
@Schema(description = "会员权益表视图")
@AutoMapper(target = MemberBenefit.class)
public class MemberBenefitVO implements Serializable {

    @Schema(description = "权益ID")
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
