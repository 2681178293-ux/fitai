package net.wanho.fitai.modules.coach.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.coach.model.po.UsrCoachBind;

@Data
@Schema(description = "用户教练绑定修改参数")
@AutoMapper(target = UsrCoachBind.class)
public class UsrCoachBindUpdateDTO implements Serializable {

    @Schema(description = "绑定ID")
    @NotNull(message = "绑定ID不能为空")
    private Long bindId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "教练ID")
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
