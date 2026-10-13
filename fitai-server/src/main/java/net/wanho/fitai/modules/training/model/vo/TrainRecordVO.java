package net.wanho.fitai.modules.training.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainRecord;

@Data
@Schema(description = "训练记录视图")
@AutoMapper(target = TrainRecord.class)
public class TrainRecordVO implements Serializable {

    @Schema(description = "记录ID")
    private Long recordId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "计划ID")
    private Long planId;
    @Schema(description = "训练日期")
    private LocalDate trainDate;
    @Schema(description = "时长分钟")
    private Integer durationMin;
    @Schema(description = "消耗kcal")
    private BigDecimal calories;
    @Schema(description = "完成度%")
    private Integer completionPct;
    @Schema(description = "训练感受 1-5")
    private Integer feeling;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "来源 app/device")
    private String source;
}
