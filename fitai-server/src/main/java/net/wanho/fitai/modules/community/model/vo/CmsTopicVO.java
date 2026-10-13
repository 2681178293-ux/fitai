package net.wanho.fitai.modules.community.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsTopic;

@Data
@Schema(description = "社区话题视图")
@AutoMapper(target = CmsTopic.class)
public class CmsTopicVO implements Serializable {

    @Schema(description = "话题ID")
    private Long topicId;
    @Schema(description = "话题名(不含#)")
    private String topicName;
    @Schema(description = "关联动态数(热门排序依据)")
    private Integer postCount;
    @Schema(description = "状态（0启用 1停用）")
    private String status;
}
