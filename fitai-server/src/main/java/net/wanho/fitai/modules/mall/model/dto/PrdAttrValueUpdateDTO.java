package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.mall.model.po.PrdAttrValue;

import java.io.Serializable;

@Data
@Schema(description = "商品属性值修改参数")
@AutoMapper(target = PrdAttrValue.class)
public class PrdAttrValueUpdateDTO implements Serializable {

    @Schema(description = "属性值ID")
    @NotNull(message = "属性值ID不能为空")
    private Long valueId;
    @Schema(description = "属性ID")
    private Long attrId;
    @Schema(description = "属性值")
    private String attrValue;
    @Schema(description = "排序")
    private String orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
