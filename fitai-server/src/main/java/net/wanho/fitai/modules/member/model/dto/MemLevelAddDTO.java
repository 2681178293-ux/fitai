package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemLevel;

@Data
@Schema(description = "新增会员等级")
@AutoMapper(target = MemLevel.class)
public class MemLevelAddDTO implements Serializable {

    @Schema(description = "等级名称")
    @NotBlank(message = "等级名称不能为空")
    private String levelName;
    @Schema(description = "等级编码")
    @NotBlank(message = "等级编码不能为空")
    private String levelCode;
    @Schema(description = "排序(越大越高)")
    private Integer levelSort;
    @Schema(description = "成长值下限")
    private Integer growthMin;
    @Schema(description = "成长值上限")
    private Integer growthMax;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
