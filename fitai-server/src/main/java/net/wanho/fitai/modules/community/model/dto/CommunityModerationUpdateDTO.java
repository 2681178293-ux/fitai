package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CommunityModeration;

@Data
@Schema(description = "社区内容审核表修改参数")
@AutoMapper(target = CommunityModeration.class)
public class CommunityModerationUpdateDTO implements Serializable {

    @Schema(description = "审核ID")
    @NotNull(message = "审核ID不能为空")
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
    private LocalDateTime auditTime;
}
