package net.wanho.fitai.modules.member.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "体测数据 KPI")
public class UsrBodyMetricKpiVO implements Serializable {

    @Schema(description = "记录总数")
    private Long recordTotal;
    @Schema(description = "覆盖用户数")
    private Long userCount;
    @Schema(description = "本月新增记录数")
    private Long monthNewCount;
    @Schema(description = "平均体脂率")
    private BigDecimal avgBodyFat;
}
