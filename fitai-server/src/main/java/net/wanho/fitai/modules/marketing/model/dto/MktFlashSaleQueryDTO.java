package net.wanho.fitai.modules.marketing.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "秒杀活动查询参数")
public class MktFlashSaleQueryDTO extends PageQuery {

    @Schema(description = "活动名称")
    private String activityName;
    @Schema(description = "状态（0未开始 1进行中 2已结束）")
    private String status;
}
