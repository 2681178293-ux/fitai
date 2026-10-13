package net.wanho.fitai.modules.community.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.community.model.po.CommunityModeration;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "社区内容审核表视图")
@AutoMapper(target = CommunityModeration.class)
public class CommunityModerationVO implements Serializable {

    @Schema(description = "审核ID")
    private Long id;
    @Schema(description = "对象类型：post/comment")
    private String targetType;
    @Schema(description = "对象ID")
    private Long targetId;
    @Schema(description = "内容快照")
    private String contentSnapshot;
    @Schema(description = "审核状态：0-待审 1-通过 2-拒绝")
    private Integer auditStatus;
    @Schema(description = "拒绝原因")
    private String auditReason;
    @Schema(description = "审核人ID")
    private Long auditorId;
    @Schema(description = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
