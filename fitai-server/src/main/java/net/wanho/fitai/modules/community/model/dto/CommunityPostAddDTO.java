package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.community.model.po.CommunityPost;

import java.io.Serializable;

@Data
@Schema(description = "新增社区动态表")
@AutoMapper(target = CommunityPost.class)
public class CommunityPostAddDTO implements Serializable {

    @Schema(description = "发布用户ID（usr_customer.customer_id）")
    @NotNull(message = "发布用户ID不能为空")
    private Long userId;
    @Schema(description = "文字内容")
    private String content;
    @Schema(description = "图片URL列表")
    private String images;
    @Schema(description = "类型：normal/checkin/challenge")
    private String type;
    @Schema(description = "关联训练计划ID")
    private Long trainingPlanId;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "评论数")
    private Integer commentCount;
    @Schema(description = "状态：0-已删除 1-正常")
    private String status;
}
