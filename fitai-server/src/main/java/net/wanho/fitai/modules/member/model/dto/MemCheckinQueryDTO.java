package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "每日签到查询参数")
public class MemCheckinQueryDTO extends PageQuery {
    @Schema(description = "用户ID")
    private Long customerId;

}
