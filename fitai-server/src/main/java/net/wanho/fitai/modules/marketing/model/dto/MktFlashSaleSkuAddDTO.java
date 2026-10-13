package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktFlashSaleSku;

@Data
@Schema(description = "新增秒杀SKU")
@AutoMapper(target = MktFlashSaleSku.class)
public class MktFlashSaleSkuAddDTO implements Serializable {

    @Schema(description = "活动ID")
    @NotNull(message = "活动ID不能为空")
    private Long activityId;
    @Schema(description = "SPU ID")
    @NotNull(message = "SPU不能为空")
    private Long spuId;
    @Schema(description = "SKU ID")
    @NotNull(message = "SKU不能为空")
    private Long skuId;
    @Schema(description = "SKU名称(冗余)")
    private String skuName;
    @Schema(description = "秒杀价")
    @NotNull(message = "秒杀价不能为空")
    private BigDecimal flashPrice;
    @Schema(description = "秒杀库存")
    @NotNull(message = "秒杀库存不能为空")
    private Integer flashStock;
    @Schema(description = "已售(冗余)")
    private Integer soldCount;
}
