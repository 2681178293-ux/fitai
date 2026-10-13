package net.wanho.fitai.modules.training.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainRecordItem;

@Data
@Schema(description = "新增训练记录明细")
@AutoMapper(target = TrainRecordItem.class)
public class TrainRecordItemAddDTO implements Serializable {

    @Schema(description = "训练记录ID")
    @NotNull(message = "训练记录ID不能为空")
    private Long recordId;
    @Schema(description = "动作ID")
    @NotNull(message = "动作ID不能为空")
    private Long actionId;
    @Schema(description = "动作名(冗余)")
    private String actionName;
    @Schema(description = "完成组数")
    private Integer setsDone;
    @Schema(description = "每组次数")
    private Integer repsDone;
    @Schema(description = "负重kg")
    private BigDecimal weightKg;
    @Schema(description = "组数明细JSON")
    private String setsJson;
    @Schema(description = "耗时秒")
    private Integer durationSec;
    @Schema(description = "消耗kcal")
    private BigDecimal calories;
    @Schema(description = "排序")
    private Integer orderNum;
}
