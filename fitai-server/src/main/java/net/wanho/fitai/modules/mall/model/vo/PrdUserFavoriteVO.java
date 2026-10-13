package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdUserFavorite;

@Data
@Schema(description = "用户商品收藏视图")
@AutoMapper(target = PrdUserFavorite.class)
public class PrdUserFavoriteVO implements Serializable {

    @Schema(description = "收藏ID")
    private Long favoriteId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "SPU ID")
    private Long spuId;
}
