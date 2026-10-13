package net.wanho.fitai.modules.marketing.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "挑战赛查询参数")
public class MktChallengeQueryDTO extends PageQuery {

    @Schema(description = "挑战名称")
    private String name;
}
