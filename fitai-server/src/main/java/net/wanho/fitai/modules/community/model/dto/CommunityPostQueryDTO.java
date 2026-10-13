package net.wanho.fitai.modules.community.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区动态表查询参数")
public class CommunityPostQueryDTO extends PageQuery {
    @Schema(description = "关键字")
    private String keyword;
    @Schema(description = "用户昵称")
    private String userNickname;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "类型")
    private String type;

}
