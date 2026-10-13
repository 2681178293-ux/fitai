package net.wanho.fitai.modules.coach.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "教练排课表(周模板+请假)查询参数")
public class UsrCoachScheduleQueryDTO extends PageQuery {

}
