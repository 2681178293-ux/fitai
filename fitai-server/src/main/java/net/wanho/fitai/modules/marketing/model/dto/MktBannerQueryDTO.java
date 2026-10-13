package net.wanho.fitai.modules.marketing.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "轮播图查询参数")
public class MktBannerQueryDTO extends PageQuery {

    @Schema(description = "标题")
    private String title;
}
