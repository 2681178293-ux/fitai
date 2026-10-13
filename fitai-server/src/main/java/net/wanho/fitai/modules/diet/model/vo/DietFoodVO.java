package net.wanho.fitai.modules.diet.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.diet.model.po.DietFood;

@Data
@Schema(description = "食物库视图")
@AutoMapper(target = DietFood.class)
public class DietFoodVO implements Serializable {

    @Schema(description = "食物ID")
    private Long foodId;
    @Schema(description = "分类ID")
    private Long categoryId;
    @Schema(description = "分类名(冗余)")
    private String categoryName;
    @Schema(description = "食物名称")
    private String foodName;
    @Schema(description = "编码")
    private String foodCode;
    @Schema(description = "计量单位")
    private String unit;
    @Schema(description = "热量kcal")
    private BigDecimal calories;
    @Schema(description = "蛋白质g")
    private BigDecimal proteinG;
    @Schema(description = "脂肪g")
    private BigDecimal fatG;
    @Schema(description = "碳水g")
    private BigDecimal carbsG;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
