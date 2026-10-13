package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.SeckillActivity;

@Data
@Schema(description = "秒杀活动表修改参数")
@AutoMapper(target = SeckillActivity.class)
public class SeckillActivityUpdateDTO implements Serializable {

    @Schema(description = "")
    @NotNull(message = "id不能为空")
    private Long id;
    @Schema(description = "备注")
    private String name;
    @Schema(description = "备注")
    private String coverImage;
    @Schema(description = "备注")
    private String description;
    @Schema(description = "备注")
    private LocalDateTime startTime;
    @Schema(description = "备注")
    private LocalDateTime endTime;
    @Schema(description = "状态：0-待开始 1-进行中 2-已结束")
    private Integer status;
}
