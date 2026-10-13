package net.wanho.fitai.modules.coach.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "教练约课预约单查询参数")
public class UsrCoachBookingQueryDTO extends PageQuery {

    @Schema(description = "教练名(冗余)")
    private String coachName;
    @Schema(description = "用户id")
    private String customerId;
    @Schema(description = "状态（0待确认 1已确认 2已完成 3已取消）")
    private String status;
    @Schema(description = "预约日期")
    private String bookingDate;
}
