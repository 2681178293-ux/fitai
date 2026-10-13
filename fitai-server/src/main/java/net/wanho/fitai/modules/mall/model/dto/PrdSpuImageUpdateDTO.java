package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSpuImage;

@Data
@Schema(description = "SPU图片修改参数")
@AutoMapper(target = PrdSpuImage.class)
public class PrdSpuImageUpdateDTO implements Serializable {

    @Schema(description = "图片ID")
    @NotNull(message = "图片ID不能为空")
    private Long imageId;
    @Schema(description = "SPU ID")
    private Long spuId;
    @Schema(description = "OSS图片ID(media_oss)")
    private Long ossId;
    @Schema(description = "图片URL(冗余,来自media_oss)")
    private String imageUrl;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "主图（0否 1是）")
    private String isMain;
}
