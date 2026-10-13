package net.wanho.fitai.modules.diet.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.diet.model.po.DietCategory;

@Data
@Schema(description = "饮食分类修改参数")
@AutoMapper(target = DietCategory.class)
public class DietCategoryUpdateDTO implements Serializable {

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    @Schema(description = "父分类")
    private Long parentId;
    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
