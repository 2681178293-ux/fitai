package net.wanho.fitai.modules.diet.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class DietRecord implements Serializable {

    @TableId
    private Long recordId;
    private Long customerId;
    private LocalDate recordDate;
    private String mealType;
    private Long foodId;
    private String foodName;
    private BigDecimal amount;
    private BigDecimal calories;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
