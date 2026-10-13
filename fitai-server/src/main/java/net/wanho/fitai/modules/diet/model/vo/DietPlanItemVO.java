package net.wanho.fitai.modules.diet.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.diet.model.po.DietPlanItem;

@Data
@Schema(description = "饮食方案明细视图")
@AutoMapper(target = DietPlanItem.class)
public class DietPlanItemVO implements Serializable {

    @Schema(description = "明细ID")
    private Long itemId;
    @Schema(description = "方案ID")
    private Long planId;
    @Schema(description = "餐次（1早 2午 3晚 4加餐）")
    private String mealType;
    @Schema(description = "食物ID")
    private Long foodId;
    @Schema(description = "食物名(冗余)")
    private String foodName;
    @Schema(description = "份量")
    private BigDecimal amount;
    @Schema(description = "热量(冗余)")
    private BigDecimal calories;
}
