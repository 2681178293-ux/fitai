package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSpuImage;

@Data
@Schema(description = "SPU图片视图")
@AutoMapper(target = PrdSpuImage.class)
public class PrdSpuImageVO implements Serializable {

    @Schema(description = "图片ID")
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
