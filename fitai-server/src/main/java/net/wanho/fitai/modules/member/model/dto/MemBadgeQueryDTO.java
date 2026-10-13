package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "徽章定义查询参数")
public class MemBadgeQueryDTO extends PageQuery {

    @Schema(description = "徽章编码")
    private String badgeCode;
    @Schema(description = "徽章名称")
    private String badgeName;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
    @Schema(description = "分类")
    private String category;
}
