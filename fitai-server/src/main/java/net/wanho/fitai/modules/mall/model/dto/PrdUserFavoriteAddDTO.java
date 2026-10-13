package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdUserFavorite;

@Data
@Schema(description = "新增用户商品收藏")
@AutoMapper(target = PrdUserFavorite.class)
public class PrdUserFavoriteAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "SPU ID")
    @NotNull(message = "SPU不能为空")
    private Long spuId;
}
