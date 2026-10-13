package net.wanho.fitai.modules.community.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsCommentLike;

@Data
@Schema(description = "评论点赞视图")
@AutoMapper(target = CmsCommentLike.class)
public class CmsCommentLikeVO implements Serializable {

    @Schema(description = "点赞ID")
    private Long likeId;
    @Schema(description = "评论ID")
    private Long commentId;
    @Schema(description = "用户ID")
    private Long customerId;
}
