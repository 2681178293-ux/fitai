package net.wanho.fitai.modules.coach.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "教练查询参数")
public class UsrCoachQueryDTO extends PageQuery {

    @Schema(description = "姓名")
    private String coachName;
    @Schema(description = "专长")
    private String specialty;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
