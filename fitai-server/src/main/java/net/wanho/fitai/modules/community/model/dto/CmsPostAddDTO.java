package net.wanho.fitai.modules.community.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.community.model.po.CmsPost;

@Data
@Schema(description = "新增社区动态")
@AutoMapper(target = CmsPost.class)
public class CmsPostAddDTO implements Serializable {

    @Schema(description = "发布用户ID")
    @NotNull(message = "发布用户ID不能为空")
    private Long customerId;
    @Schema(description = "昵称(冗余)")
    private String customerName;
    @Schema(description = "正文")
    @NotBlank(message = "正文不能为空")
    private String content;
    @Schema(description = "图片列表")
    private String imagesJson;
    @Schema(description = "点赞数(冗余)")
    private Integer likeCount;
    @Schema(description = "评论数(冗余)")
    private Integer commentCount;
    @Schema(description = "审核（0待审 1通过 2拒绝）")
    private String auditStatus;
    @Schema(description = "状态（0正常 1隐藏）")
    private String status;
}
