package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.SeckillSku;

@Data
@Schema(description = "秒杀商品表修改参数")
@AutoMapper(target = SeckillSku.class)
public class SeckillSkuUpdateDTO implements Serializable {

    @Schema(description = "")
    @NotNull(message = "id不能为空")
    private Long id;
    @Schema(description = "")
    private Long activityId;
    @Schema(description = "")
    private Long skuId;
    @Schema(description = "备注")
    private BigDecimal seckillPrice;
    @Schema(description = "备注")
    private Integer totalStock;
    @Schema(description = "备注")
    private Integer availableStock;
    @Schema(description = "备注")
    private Integer limitPerUser;
    @Schema(description = "")
    private Integer sortOrder;
    @Schema(description = "")
    private Integer status;
}
