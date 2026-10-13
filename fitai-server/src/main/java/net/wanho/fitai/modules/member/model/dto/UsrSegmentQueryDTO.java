package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分群查询参数")
public class UsrSegmentQueryDTO extends PageQuery {

    @Schema(description = "分群名称")
    private String segmentName;
    @Schema(description = "分群编码")
    private String segmentCode;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
