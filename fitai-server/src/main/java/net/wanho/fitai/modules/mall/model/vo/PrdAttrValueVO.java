package net.wanho.fitai.modules.mall.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdAttrValue;

@Data
@Schema(description = "商品属性值视图")
@AutoMapper(target = PrdAttrValue.class)
public class PrdAttrValueVO implements Serializable {

    @Schema(description = "属性值ID")
    private Long valueId;
    @Schema(description = "属性ID")
    private Long attrId;
    @Schema(description = "属性值")
    private String attrValue;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
