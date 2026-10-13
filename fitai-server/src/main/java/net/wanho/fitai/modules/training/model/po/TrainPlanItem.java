package net.wanho.fitai.modules.training.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

@Data
public class TrainPlanItem implements Serializable {

    @TableId
    private Long itemId;
    private Long planId;
    private Integer weekNo;
    private Integer dayNo;
    private Long actionId;
    private String actionName;
    private Integer sets;
    private String reps;
    private Integer restSec;
    private Integer orderNum;
}
