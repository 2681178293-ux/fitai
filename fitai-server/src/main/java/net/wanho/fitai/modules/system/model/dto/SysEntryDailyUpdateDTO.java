package net.wanho.fitai.modules.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysEntryDaily;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "修改入口日统计参数")
@AutoMapper(target = SysEntryDaily.class)
public class SysEntryDailyUpdateDTO implements Serializable {
    @Schema(description = "统计日期")
    @NotNull(message = "统计日期不能为空")
    private LocalDate statDate;
    @Schema(description = "渠道")
    @NotBlank(message = "渠道不能为空")
    private String channel;
    @Schema(description = "访问次数")
    private Integer visitCount;
}
