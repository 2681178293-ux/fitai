package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.SeckillActivity;

@Data
@Schema(description = "新增秒杀活动表")
@AutoMapper(target = SeckillActivity.class)
public class SeckillActivityAddDTO implements Serializable {

    @Schema(description = "备注")
    @NotBlank(message = "备注不能为空")
    private String name;
    @Schema(description = "备注")
    private String coverImage;
    @Schema(description = "备注")
    private String description;
    @Schema(description = "备注")
    @NotNull(message = "备注不能为空")
    private LocalDateTime startTime;
    @Schema(description = "备注")
    @NotNull(message = "备注不能为空")
    private LocalDateTime endTime;
    @Schema(description = "状态：0-待开始 1-进行中 2-已结束")
    private Integer status;
}
