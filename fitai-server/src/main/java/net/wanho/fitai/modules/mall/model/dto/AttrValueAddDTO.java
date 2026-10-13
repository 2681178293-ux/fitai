package net.wanho.fitai.modules.mall.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import net.wanho.fitai.modules.mall.model.po.PrdAttrValue;

@Data
@AutoMapper(target = PrdAttrValue.class)
public class AttrValueAddDTO {

    private String attrValue;
    private String orderNum;
    private String status;
}
