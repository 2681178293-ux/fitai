package net.wanho.fitai.modules.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SysNotice查询参数")
public class SysNoticeQueryDTO extends PageQuery {
    @Schema(description = "公告标题")
    private String noticeTitle;
    @Schema(description = "公告类型")
    private String noticeType;
    @Schema(description = "状态")
    private String status;

}
