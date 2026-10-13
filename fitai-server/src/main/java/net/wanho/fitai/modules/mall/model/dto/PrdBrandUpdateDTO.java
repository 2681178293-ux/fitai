package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdBrand;

@Data
@Schema(description = "商品品牌修改参数")
@AutoMapper(target = PrdBrand.class)
public class PrdBrandUpdateDTO implements Serializable {

    @Schema(description = "品牌ID")
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;
    @Schema(description = "品牌名称")
    private String brandName;
    @Schema(description = "Logo URL")
    private String brandLogo;
    @Schema(description = "品牌描述")
    private String description;
    @Schema(description = "首字母(检索)")
    private String firstLetter;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
