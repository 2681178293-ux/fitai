package net.wanho.fitai.modules.training.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "训练计划明细查询参数")
public class TrainPlanItemQueryDTO extends PageQuery {

    @Schema(description = "动作名(冗余)")
    private String actionName;
}
