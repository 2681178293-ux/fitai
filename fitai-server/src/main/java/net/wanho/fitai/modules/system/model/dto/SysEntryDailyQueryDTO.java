package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "入口日统计查询参数")
public class SysEntryDailyQueryDTO extends PageQuery {
    @Schema(description = "渠道")
    private String channel;
    @Schema(description = "统计日期")
    private LocalDate statDate;
}
