package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysNoticeRead查询参数")
public class SysNoticeReadQueryDTO extends PageQuery {
    @Schema(description = "公告ID")
    private Long noticeId;
    @Schema(description = "用户ID")
    private Long customerId;

}
