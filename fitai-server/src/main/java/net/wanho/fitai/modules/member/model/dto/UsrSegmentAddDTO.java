package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrSegment;

@Data
@Schema(description = "新增用户分群")
@AutoMapper(target = UsrSegment.class)
public class UsrSegmentAddDTO implements Serializable {

    @Schema(description = "分群名称")
    @NotBlank(message = "分群名称不能为空")
    private String segmentName;
    @Schema(description = "分群编码")
    @NotBlank(message = "分群编码不能为空")
    private String segmentCode;
    @Schema(description = "规则JSON")
    private String ruleJson;
    @Schema(description = "用户数(冗余)")
    private Integer userCount;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
