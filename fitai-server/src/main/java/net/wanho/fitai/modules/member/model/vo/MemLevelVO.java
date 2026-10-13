package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.member.model.po.MemLevel;

import java.io.Serializable;

@Data
@Schema(description = "会员等级视图")
@AutoMapper(target = MemLevel.class)
public class MemLevelVO implements Serializable {

    @Schema(description = "等级ID")
    private Long levelId;
    @Schema(description = "等级名称")
    private String levelName;
    @Schema(description = "等级编码")
    private String levelCode;
    @Schema(description = "排序(越大越高)")
    private Integer levelSort;
    @Schema(description = "成长值下限")
    private Integer growthMin;
    @Schema(description = "成长值上限")
    private Integer growthMax;
    @Schema(description = "图标")
    private String iconUrl;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
