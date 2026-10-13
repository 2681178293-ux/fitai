package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrSegmentRel;

@Data
@Schema(description = "用户分群关联视图")
@AutoMapper(target = UsrSegmentRel.class)
public class UsrSegmentRelVO implements Serializable {

    @Schema(description = "关联ID")
    private Long segmentRelId;
    @Schema(description = "分群ID")
    private Long segmentId;
    @Schema(description = "用户ID")
    private Long customerId;
}
