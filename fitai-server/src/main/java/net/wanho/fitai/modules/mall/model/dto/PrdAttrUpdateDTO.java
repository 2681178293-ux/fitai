package net.wanho.fitai.modules.mall.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.modules.mall.model.po.PrdAttr;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "商品属性修改参数")
@AutoMapper(target = PrdAttr.class)
public class PrdAttrUpdateDTO implements Serializable {

    @Schema(description = "属性ID")
    @NotNull(message = "属性ID不能为空")
    private Long attrId;
    @Schema(description = "属性名(如口味/尺码)")
    private String attrName;
    @Schema(description = "类型（1销售属性 2基本属性）")
    private String attrType;
    @Schema(description = "录入方式（1单选 2多选 3手工输入）")
    private String selectType;
    @Schema(description = "排序")
    private String orderNum;
    @Schema(description = "状态（0正常 1停用）")
    private String status;

    private List<PrdAttrValueUpdateDTO> values;
}
