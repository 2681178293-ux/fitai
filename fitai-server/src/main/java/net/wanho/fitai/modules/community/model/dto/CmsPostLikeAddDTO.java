package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsPostLike;

@Data
@Schema(description = "新增动态点赞")
@AutoMapper(target = CmsPostLike.class)
public class CmsPostLikeAddDTO implements Serializable {

    @Schema(description = "动态ID")
    @NotNull(message = "动态ID不能为空")
    private Long postId;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
}
