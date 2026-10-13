package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdUserFavorite;

@Data
@Schema(description = "用户商品收藏修改参数")
@AutoMapper(target = PrdUserFavorite.class)
public class PrdUserFavoriteUpdateDTO implements Serializable {

    @Schema(description = "收藏ID")
    @NotNull(message = "收藏ID不能为空")
    private Long favoriteId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "SPU ID")
    private Long spuId;
}
