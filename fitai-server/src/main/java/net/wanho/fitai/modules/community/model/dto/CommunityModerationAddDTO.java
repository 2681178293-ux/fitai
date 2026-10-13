package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CommunityModeration;

@Data
@Schema(description = "新增社区内容审核表")
@AutoMapper(target = CommunityModeration.class)
public class CommunityModerationAddDTO implements Serializable {

    @Schema(description = "对象类型：post/comment")
    @NotBlank(message = "对象类型不能为空")
    private String targetType;
    @Schema(description = "对象ID")
    @NotNull(message = "对象ID不能为空")
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
