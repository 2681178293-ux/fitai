package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemBadge;

@Data
@Schema(description = "徽章定义修改参数")
@AutoMapper(target = MemBadge.class)
public class MemBadgeUpdateDTO implements Serializable {

    @Schema(description = "徽章ID")
    @NotNull(message = "徽章ID不能为空")
    private Long badgeId;
    @Schema(description = "徽章编码")
    private String badgeCode;
    @Schema(description = "徽章名称")
    private String badgeName;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "图标URL")
    private String iconUrl;
    @Schema(description = "分类 training/social/achievement")
    private String category;
    @Schema(description = "条件类型 train_days/checkin/points")
    private String conditionType;
    @Schema(description = "条件值")
    private Integer conditionValue;
    @Schema(description = "奖励积分")
    private Integer pointsReward;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
