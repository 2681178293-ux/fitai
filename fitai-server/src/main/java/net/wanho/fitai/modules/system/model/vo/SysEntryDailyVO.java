package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysEntryDaily;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "SysEntryDaily视图")
@AutoMapper(target = SysEntryDaily.class)
public class SysEntryDailyVO implements Serializable {
    @Schema(description = "统计日期")
    private LocalDate statDate;
    @Schema(description = "渠道")
    private String channel;
    @Schema(description = "访问次数")
    private Integer visitCount;

}
