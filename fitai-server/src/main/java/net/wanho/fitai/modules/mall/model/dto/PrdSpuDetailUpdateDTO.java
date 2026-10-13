package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSpuDetail;

@Data
@Schema(description = "SPU详情修改参数")
@AutoMapper(target = PrdSpuDetail.class)
public class PrdSpuDetailUpdateDTO implements Serializable {

    @Schema(description = "详情ID")
    @NotNull(message = "详情ID不能为空")
    private Long detailId;
    @Schema(description = "SPU ID")
    private Long spuId;
    @Schema(description = "简介")
    private String description;
    @Schema(description = "详情富文本")
    private String detailHtml;
}
