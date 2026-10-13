package net.wanho.fitai.modules.marketing.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class MktChallengeRecord implements Serializable {

    @TableId
    private Long id;
    private Long challengeId;
    private Long userId;
    private Integer currentDays;
    private Integer currentValue;
    private Integer completed;
    private Integer cheatFlag;
    private LocalDateTime joinTime;
    private LocalDateTime lastCheckinTime;
}
