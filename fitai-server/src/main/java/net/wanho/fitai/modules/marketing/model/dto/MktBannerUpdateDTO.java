package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktBanner;

@Data
@Schema(description = "轮播图修改参数")
@AutoMapper(target = MktBanner.class)
public class MktBannerUpdateDTO implements Serializable {

    @Schema(description = "轮播ID")
    @NotNull(message = "轮播ID不能为空")
    private Long id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "图片URL")
    private String imageUrl;
    @Schema(description = "链接类型：url/page/product/course")
    private String linkType;
    @Schema(description = "链接值")
    private String linkValue;
    @Schema(description = "位置：home/course/product")
    private String position;
    @Schema(description = "排序（越小越靠前）")
    private Integer sortOrder;
    @Schema(description = "有效期开始")
    private LocalDateTime startTime;
    @Schema(description = "有效期结束")
    private LocalDateTime endTime;
    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}
