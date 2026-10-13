package net.wanho.fitai.modules.diet.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;
import net.wanho.fitai.modules.diet.model.po.DietRecord;

@Data
@Schema(description = "新增饮食记录")
@AutoMapper(target = DietRecord.class)
public class DietRecordAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "记录日期")
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;
    @Schema(description = "餐次（1早 2午 3晚 4加餐）")
    @NotBlank(message = "餐次不能为空")
    private String mealType;
    @Schema(description = "食物ID")
    @NotNull(message = "食物ID不能为空")
    private Long foodId;
    @Schema(description = "食物名(冗余)")
    private String foodName;
    @Schema(description = "份量")
    @NotNull(message = "份量不能为空")
    private BigDecimal amount;
    @Schema(description = "热量")
    @NotNull(message = "热量不能为空")
    private BigDecimal calories;
}
