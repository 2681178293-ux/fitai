package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsCommentLike;

@Data
@Schema(description = "新增评论点赞")
@AutoMapper(target = CmsCommentLike.class)
public class CmsCommentLikeAddDTO implements Serializable {

    @Schema(description = "评论ID")
    @NotNull(message = "评论ID不能为空")
    private Long commentId;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
}
