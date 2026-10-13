package net.wanho.fitai.modules.training.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;

@Data
public class TrainRecordItem implements Serializable {

    @TableId
    private Long itemId;
    private Long recordId;
    private Long actionId;
    private String actionName;
    private Integer setsDone;
    private Integer repsDone;
    private BigDecimal weightKg;
    private String setsJson;
    private Integer durationSec;
    private BigDecimal calories;
    private Integer orderNum;
}
