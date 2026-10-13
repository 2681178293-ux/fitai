package net.wanho.fitai.modules.diet.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.diet.model.po.DietCategory;

@Data
@Schema(description = "新增饮食分类")
@AutoMapper(target = DietCategory.class)
public class DietCategoryAddDTO implements Serializable {

    @Schema(description = "父分类")
    private Long parentId;
    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
