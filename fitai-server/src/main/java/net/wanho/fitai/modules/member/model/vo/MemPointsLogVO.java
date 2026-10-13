package net.wanho.fitai.modules.member.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.member.model.po.MemPointsLog;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "积分流水视图")
@AutoMapper(target = MemPointsLog.class)
public class MemPointsLogVO implements Serializable {

    @Schema(description = "流水ID")
    private Long logId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "规则ID")
    private Long ruleId;
    @Schema(description = "变动积分")
    private Integer changePoints;
    @Schema(description = "变动后余额")
    private Integer balanceAfter;
    @Schema(description = "来源")
    private String registerSource;
    @Schema(description = "业务类型")
    private String bizType;
    @Schema(description = "业务ID")
    private Long bizId;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
