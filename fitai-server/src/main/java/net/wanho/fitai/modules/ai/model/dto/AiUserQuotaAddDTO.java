package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiUserQuota;

@Data
@Schema(description = "新增AI用户月额度")
@AutoMapper(target = AiUserQuota.class)
public class AiUserQuotaAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "额度月份 yyyy-MM")
    @NotBlank(message = "额度月份不能为空")
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
