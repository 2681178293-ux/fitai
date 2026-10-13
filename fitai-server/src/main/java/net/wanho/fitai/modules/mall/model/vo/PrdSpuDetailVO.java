package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdSpuDetail;

@Data
@Schema(description = "SPU详情视图")
@AutoMapper(target = PrdSpuDetail.class)
public class PrdSpuDetailVO implements Serializable {

    @Schema(description = "详情ID")
    private Long detailId;
    @Schema(description = "SPU ID")
    private Long spuId;
    @Schema(description = "简介")
    private String description;
    @Schema(description = "详情富文本")
    private String detailHtml;
}
