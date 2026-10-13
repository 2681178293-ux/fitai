package net.wanho.fitai.modules.marketing.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户优惠券领取记录查询参数")
public class MktCouponUserQueryDTO extends PageQuery {

    @Schema(description = "使用订单号")
    private String orderNo;
}
