package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSpuDetail;

@Data
@Schema(description = "新增SPU详情")
@AutoMapper(target = PrdSpuDetail.class)
public class PrdSpuDetailAddDTO implements Serializable {

    @Schema(description = "SPU ID")
    @NotNull(message = "SPU不能为空")
    private Long spuId;
    @Schema(description = "简介")
    private String description;
    @Schema(description = "详情富文本")
    private String detailHtml;
}
