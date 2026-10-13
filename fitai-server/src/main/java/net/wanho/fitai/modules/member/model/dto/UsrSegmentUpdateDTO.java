package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrSegment;

@Data
@Schema(description = "用户分群修改参数")
@AutoMapper(target = UsrSegment.class)
public class UsrSegmentUpdateDTO implements Serializable {

    @Schema(description = "分群ID")
    @NotNull(message = "分群ID不能为空")
    private Long segmentId;
    @Schema(description = "分群名称")
    private String segmentName;
    @Schema(description = "分群编码")
    private String segmentCode;
    @Schema(description = "规则JSON")
    private String ruleJson;
    @Schema(description = "用户数(冗余)")
    private Integer userCount;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
