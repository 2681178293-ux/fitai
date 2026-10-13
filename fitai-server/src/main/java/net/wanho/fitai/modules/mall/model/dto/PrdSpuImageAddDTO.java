package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSpuImage;

@Data
@Schema(description = "新增SPU图片")
@AutoMapper(target = PrdSpuImage.class)
public class PrdSpuImageAddDTO implements Serializable {

    @Schema(description = "SPU ID")
    @NotNull(message = "SPU不能为空")
    private Long spuId;
    @Schema(description = "OSS图片ID(media_oss)")
    private Long ossId;
    @Schema(description = "图片URL(冗余,来自media_oss)")
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "主图（0否 1是）")
    private String isMain;
}
