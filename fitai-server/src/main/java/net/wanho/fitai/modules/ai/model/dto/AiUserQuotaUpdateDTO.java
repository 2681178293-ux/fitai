package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiUserQuota;

@Data
@Schema(description = "AI用户月额度修改参数")
@AutoMapper(target = AiUserQuota.class)
public class AiUserQuotaUpdateDTO implements Serializable {

    @Schema(description = "额度ID")
    @NotNull(message = "额度ID不能为空")
    private Long quotaId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "额度月份 yyyy-MM")
    private String quotaMonth;
    @Schema(description = "月总额度(Token)")
    private Integer quotaTotal;
    @Schema(description = "已用 Token")
    private Integer quotaUsed;
    @Schema(description = "剩余 Token(冗余)")
    private Integer quotaRemain;
    @Schema(description = "月基础来源 base/member（赠送见 ai_quota_gift）")
    private String sourceType;
    @Schema(description = "来源ID")
    private Long sourceId;
}
