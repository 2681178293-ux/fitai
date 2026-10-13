package net.wanho.fitai.modules.community.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsComment;

@Data
@Schema(description = "社区评论视图")
@AutoMapper(target = CmsComment.class)
public class CmsCommentVO implements Serializable {

    @Schema(description = "评论ID")
    private Long commentId;
    @Schema(description = "动态ID")
    private Long postId;
    @Schema(description = "评论用户ID")
    private Long customerId;
    @Schema(description = "昵称(冗余)")
    private String customerName;
    @Schema(description = "父评论ID")
    private Long parentId;
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "点赞数(冗余)")
    private Integer likeCount;
    @Schema(description = "审核（0待审 1通过 2拒绝）")
    private String auditStatus;
    @Schema(description = "状态（0正常 1隐藏）")
    private String status;
}
