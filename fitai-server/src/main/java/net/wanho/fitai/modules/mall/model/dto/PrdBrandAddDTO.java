package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdBrand;

@Data
@Schema(description = "新增商品品牌")
@AutoMapper(target = PrdBrand.class)
public class PrdBrandAddDTO implements Serializable {

    @Schema(description = "品牌名称")
    @NotBlank(message = "品牌名称不能为空")
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
