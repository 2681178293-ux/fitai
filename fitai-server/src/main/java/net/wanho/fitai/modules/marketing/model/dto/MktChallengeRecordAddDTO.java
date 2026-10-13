package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktChallengeRecord;

@Data
@Schema(description = "新增挑战赛参与记录")
@AutoMapper(target = MktChallengeRecord.class)
public class MktChallengeRecordAddDTO implements Serializable {

    @Schema(description = "挑战ID")
    @NotNull(message = "挑战ID不能为空")
    private Long challengeId;
    @Schema(description = "用户ID（usr_customer.customer_id）")
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @Schema(description = "当前完成天数")
    private Integer currentDays;
    @Schema(description = "当前完成值")
    private Integer currentValue;
    @Schema(description = "是否完成：0否 1是")
    private Integer completed;
    @Schema(description = "作弊标记：0正常 1作弊")
    private Integer cheatFlag;
    @Schema(description = "参与时间")
    private LocalDateTime joinTime;
    @Schema(description = "最后打卡时间")
    private LocalDateTime lastCheckinTime;
}
