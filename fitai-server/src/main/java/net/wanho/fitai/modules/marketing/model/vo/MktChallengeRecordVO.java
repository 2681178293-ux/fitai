package net.wanho.fitai.modules.marketing.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktChallengeRecord;

@Data
@Schema(description = "挑战赛参与记录视图")
@AutoMapper(target = MktChallengeRecord.class)
public class MktChallengeRecordVO implements Serializable {

    @Schema(description = "记录ID")
    private Long id;
    @Schema(description = "挑战ID")
    private Long challengeId;
    @Schema(description = "用户ID（usr_customer.customer_id）")
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
