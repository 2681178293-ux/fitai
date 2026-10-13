package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户会员订阅查询参数")
public class MemSubscriptionQueryDTO extends PageQuery {

    @Schema(description = "会员ID")
    private Long customerId;
    @Schema(description = "套餐名(冗余)")
    private String packageName;
    @Schema(description = "状态（0有效 1已过期 2已取消）")
    private String status;
}
