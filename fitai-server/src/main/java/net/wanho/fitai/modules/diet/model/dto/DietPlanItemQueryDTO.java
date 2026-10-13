package net.wanho.fitai.modules.diet.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "饮食方案明细查询参数")
public class DietPlanItemQueryDTO extends PageQuery {

    @Schema(description = "食物名(冗余)")
    private String foodName;
}
