package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsPostLike;

@Data
@Schema(description = "动态点赞修改参数")
@AutoMapper(target = CmsPostLike.class)
public class CmsPostLikeUpdateDTO implements Serializable {

    @Schema(description = "点赞ID")
    @NotNull(message = "点赞ID不能为空")
    private Long likeId;
    @Schema(description = "动态ID")
    private Long postId;
    @Schema(description = "用户ID")
    private Long customerId;
}
