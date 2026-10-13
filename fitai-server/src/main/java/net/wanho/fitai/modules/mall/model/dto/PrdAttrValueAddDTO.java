package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.mall.model.po.PrdAttrValue;

@Data
@Schema(description = "新增商品属性值")
@AutoMapper(target = PrdAttrValue.class)
public class PrdAttrValueAddDTO implements Serializable {

    @Schema(description = "属性ID")
    @NotNull(message = "属性ID不能为空")
    private Long attrId;
    @Schema(description = "属性值")
    @NotBlank(message = "属性值不能为空")
    private String attrValue;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
