package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CommunityComment;

@Data
@Schema(description = "新增社区评论表")
@AutoMapper(target = CommunityComment.class)
public class CommunityCommentAddDTO implements Serializable {

    @Schema(description = "动态ID")
    @NotNull(message = "动态ID不能为空")
    private Long postId;
    @Schema(description = "评论用户ID")
    @NotNull(message = "评论用户ID不能为空")
    private Long userId;
    @Schema(description = "父评论ID")
    private Long parentId;
    @Schema(description = "回复目标用户ID")
    private Long replyUserId;
    @Schema(description = "评论内容")
    @NotBlank(message = "评论内容不能为空")
    private String content;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "状态：0-已删除 1-正常")
    private Integer status;
    @Schema(description = "是否置顶：0-否 1-是")
    private Integer top;
}
