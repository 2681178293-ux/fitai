package net.wanho.fitai.modules.training.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainPlanItem;

@Data
@Schema(description = "训练计划明细视图")
@AutoMapper(target = TrainPlanItem.class)
public class TrainPlanItemVO implements Serializable {

    @Schema(description = "明细ID")
    private Long itemId;
    @Schema(description = "计划ID")
    private Long planId;
    @Schema(description = "第几周")
    private Integer weekNo;
    @Schema(description = "第几天")
    private Integer dayNo;
    @Schema(description = "动作ID")
    private Long actionId;
    @Schema(description = "动作名(冗余)")
    private String actionName;
    @Schema(description = "组数")
    private Integer sets;
    @Schema(description = "每组次数/时长 如12、10-12、30秒")
    private String reps;
    @Schema(description = "休息秒")
    private Integer restSec;
    @Schema(description = "排序")
    private Integer orderNum;
}
