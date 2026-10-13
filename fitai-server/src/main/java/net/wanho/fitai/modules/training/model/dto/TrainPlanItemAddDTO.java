package net.wanho.fitai.modules.training.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainPlanItem;

@Data
@Schema(description = "新增训练计划明细")
@AutoMapper(target = TrainPlanItem.class)
public class TrainPlanItemAddDTO implements Serializable {

    @Schema(description = "计划ID")
    @NotNull(message = "计划ID不能为空")
    private Long planId;
    @Schema(description = "第几周")
    @NotNull(message = "第几周不能为空")
    private Integer weekNo;
    @Schema(description = "第几天")
    @NotNull(message = "第几天不能为空")
    private Integer dayNo;
    @Schema(description = "动作ID")
    @NotNull(message = "动作ID不能为空")
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
