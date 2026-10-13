package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsTopic;

@Data
@Schema(description = "社区话题修改参数")
@AutoMapper(target = CmsTopic.class)
public class CmsTopicUpdateDTO implements Serializable {

    @Schema(description = "话题ID")
    @NotNull(message = "话题ID不能为空")
    private Long topicId;
    @Schema(description = "话题名(不含#)")
    private String topicName;
    @Schema(description = "关联动态数(热门排序依据)")
    private Integer postCount;
    @Schema(description = "状态（0启用 1停用）")
    private String status;
}
