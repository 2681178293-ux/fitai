package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiQuotaGift;

@Data
@Schema(description = "AI配额赠送流水修改参数")
@AutoMapper(target = AiQuotaGift.class)
public class AiQuotaGiftUpdateDTO implements Serializable {

    @Schema(description = "赠送ID")
    @NotNull(message = "赠送ID不能为空")
    private Long giftId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "额度月份 yyyy-MM")
    private String quotaMonth;
    @Schema(description = "赠送 Token")
    private Integer giftTokens;
    @Schema(description = "关联权益ID")
    private Long benefitId;
    @Schema(description = "原因")
    private String reason;
}
