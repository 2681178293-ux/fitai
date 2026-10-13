package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.mall.model.po.PrdBrand;

import java.io.Serializable;

@Data
@Schema(description = "商品品牌视图")
@AutoMapper(target = PrdBrand.class)
public class PrdBrandVO implements Serializable {

    @Schema(description = "品牌ID")
    private Long brandId;
    @Schema(description = "品牌名称")
    private String brandName;
    @Schema(description = "Logo URL")
    private String logoUrl;
    @Schema(description = "品牌描述")
    private String description;
    @Schema(description = "首字母(检索)")
    private String firstLetter;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}