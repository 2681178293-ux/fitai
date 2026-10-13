package net.wanho.fitai.modules.community.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区内容审核表查询参数")
public class CommunityModerationQueryDTO extends PageQuery {
    @Schema(description = "审核状态", example = "0")
    private Long auditStatus;
    @Schema(description = "审核目标类型", example = "post")
    private String targetType;

}
