package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrSegmentRel;

@Data
@Schema(description = "新增用户分群关联")
@AutoMapper(target = UsrSegmentRel.class)
public class UsrSegmentRelAddDTO implements Serializable {

    @Schema(description = "分群ID")
    @NotNull(message = "分群ID不能为空")
    private Long segmentId;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
}
