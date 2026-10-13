package net.wanho.fitai.modules.community.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsPostLike;

@Data
@Schema(description = "动态点赞视图")
@AutoMapper(target = CmsPostLike.class)
public class CmsPostLikeVO implements Serializable {

    @Schema(description = "点赞ID")
    private Long likeId;
    @Schema(description = "动态ID")
    private Long postId;
    @Schema(description = "用户ID")
    private Long customerId;
}
