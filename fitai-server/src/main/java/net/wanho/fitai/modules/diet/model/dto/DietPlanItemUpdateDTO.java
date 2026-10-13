package net.wanho.fitai.modules.diet.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.diet.model.po.DietPlanItem;

@Data
@Schema(description = "饮食方案明细修改参数")
@AutoMapper(target = DietPlanItem.class)
public class DietPlanItemUpdateDTO implements Serializable {

    @Schema(description = "明细ID")
    @NotNull(message = "明细ID不能为空")
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
