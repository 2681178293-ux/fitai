package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiQuotaGift;

@Data
@Schema(description = "新增AI配额赠送流水")
@AutoMapper(target = AiQuotaGift.class)
public class AiQuotaGiftAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "额度月份 yyyy-MM")
    @NotBlank(message = "额度月份不能为空")
    private String quotaMonth;
    @Schema(description = "赠送 Token")
    private Integer giftTokens;
    @Schema(description = "关联权益ID")
    private Long benefitId;
    @Schema(description = "原因")
    private String reason;
}
