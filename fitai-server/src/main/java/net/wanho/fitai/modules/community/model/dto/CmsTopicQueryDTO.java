package net.wanho.fitai.modules.community.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区话题查询参数")
public class CmsTopicQueryDTO extends PageQuery {

    @Schema(description = "话题名(不含#)")
    private String topicName;
    @Schema(description = "状态（0启用 1停用）")
    private String status;
}
