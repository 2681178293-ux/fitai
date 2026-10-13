package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrSegmentRel;

@Data
@Schema(description = "用户分群关联修改参数")
@AutoMapper(target = UsrSegmentRel.class)
public class UsrSegmentRelUpdateDTO implements Serializable {

    @Schema(description = "关联ID")
    @NotNull(message = "关联ID不能为空")
    private Long segmentRelId;
    @Schema(description = "分群ID")
    private Long segmentId;
    @Schema(description = "用户ID")
    private Long customerId;
}
