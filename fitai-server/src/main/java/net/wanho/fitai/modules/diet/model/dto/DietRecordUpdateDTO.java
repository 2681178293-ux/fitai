package net.wanho.fitai.modules.diet.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;
import net.wanho.fitai.modules.diet.model.po.DietRecord;

@Data
@Schema(description = "饮食记录修改参数")
@AutoMapper(target = DietRecord.class)
public class DietRecordUpdateDTO implements Serializable {

    @Schema(description = "记录ID")
    @NotNull(message = "记录ID不能为空")
    private Long recordId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "记录日期")
    private LocalDate recordDate;
    @Schema(description = "餐次（1早 2午 3晚 4加餐）")
    private String mealType;
    @Schema(description = "食物ID")
    private Long foodId;
    @Schema(description = "食物名(冗余)")
    private String foodName;
    @Schema(description = "份量")
    private BigDecimal amount;
    @Schema(description = "热量")
    private BigDecimal calories;
}
