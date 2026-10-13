package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CommunityComment;

@Data
@Schema(description = "社区评论表修改参数")
@AutoMapper(target = CommunityComment.class)
public class CommunityCommentUpdateDTO implements Serializable {

    @Schema(description = "评论ID")
    @NotNull(message = "评论ID不能为空")
    private Long id;
    @Schema(description = "动态ID")
    private Long postId;
    @Schema(description = "评论用户ID")
    private Long userId;
    @Schema(description = "父评论ID")
    private Long parentId;
    @Schema(description = "回复目标用户ID")
    private Long replyUserId;
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "状态：0-已删除 1-正常")
    private Integer status;
    @Schema(description = "是否置顶：0-否 1-是")
    private Integer top;
}
