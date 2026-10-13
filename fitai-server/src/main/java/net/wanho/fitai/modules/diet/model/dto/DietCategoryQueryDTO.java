package net.wanho.fitai.modules.diet.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "饮食分类查询参数")
public class DietCategoryQueryDTO extends PageQuery {

    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
