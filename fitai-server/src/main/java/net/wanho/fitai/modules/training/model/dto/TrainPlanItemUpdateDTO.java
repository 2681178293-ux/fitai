package net.wanho.fitai.modules.training.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainPlanItem;

@Data
@Schema(description = "训练计划明细修改参数")
@AutoMapper(target = TrainPlanItem.class)
public class TrainPlanItemUpdateDTO implements Serializable {

    @Schema(description = "明细ID")
    @NotNull(message = "明细ID不能为空")
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
