package net.wanho.fitai.modules.diet.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;

@Data
public class DietPlanItem implements Serializable {

    @TableId
    private Long itemId;
    private Long planId;
    private String mealType;
    private Long foodId;
    private String foodName;
    private BigDecimal amount;
    private BigDecimal calories;
}
