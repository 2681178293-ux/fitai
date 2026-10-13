package net.wanho.fitai.modules.coach.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.coach.model.po.UsrCoachBind;

@Data
@Schema(description = "新增用户教练绑定")
@AutoMapper(target = UsrCoachBind.class)
public class UsrCoachBindAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "教练ID")
    @NotNull(message = "教练ID不能为空")
    private Long coachId;
    @Schema(description = "教练名(冗余)")
    private String coachName;
    @Schema(description = "类型（1跟练 2私教）")
    private String bindType;
    @Schema(description = "状态（0有效 1已解绑）")
    private String status;
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
