package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.member.model.po.UsrBodyMetric;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "体测数据视图")
@AutoMapper(target = UsrBodyMetric.class)
public class UsrBodyMetricVO implements Serializable {

    @Schema(description = "体测ID")
    private Long metricId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "记录日期")
    private LocalDate recordDate;
    @Schema(description = "身高cm")
    private BigDecimal heightCm;
    @Schema(description = "体重kg")
    private BigDecimal weightKg;
    @Schema(description = "BMI")
    private BigDecimal bmi;
    @Schema(description = "体脂率%")
    private BigDecimal bodyFatPct;
    @Schema(description = "肌肉量kg")
    private BigDecimal muscleKg;
    @Schema(description = "腰围cm")
    private BigDecimal waistCm;
    @Schema(description = "胸围cm")
    private BigDecimal chestCm;
    @Schema(description = "臀围cm")
    private BigDecimal hipCm;
    @Schema(description = "臂围cm")
    private BigDecimal armCm;
    @Schema(description = "大腿围cm")
    private BigDecimal thighCm;
    @Schema(description = "来源 manual/device/ai")
    private String source;
    @Schema(description = "设备ID")
    private Long deviceId;
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "用户昵称")
    private String nickName;
}
