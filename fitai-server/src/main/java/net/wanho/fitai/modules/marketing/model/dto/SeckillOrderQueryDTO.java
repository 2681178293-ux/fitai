package net.wanho.fitai.modules.marketing.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "秒杀订单表查询参数")
public class SeckillOrderQueryDTO extends PageQuery {

    @Schema(description = "")
    private String orderNo;
}
