package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "积分流水查询参数")
public class MemPointsLogQueryDTO extends PageQuery {
    @Schema(description = "会员ID")
    private Long customerId;
    @Schema(description = "类型")
    private String bizType;
    @Schema(description = "来源")
    private String registerSource;
    @Schema(description = "积分变动开始时间")
    private String pointsStartTime;
    @Schema(description = "积分变动结束时间")
    private String pointsEndTime;

}
