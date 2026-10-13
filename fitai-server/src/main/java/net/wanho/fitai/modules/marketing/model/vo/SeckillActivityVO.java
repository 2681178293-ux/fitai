package net.wanho.fitai.modules.marketing.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.SeckillActivity;

@Data
@Schema(description = "秒杀活动表视图")
@AutoMapper(target = SeckillActivity.class)
public class SeckillActivityVO implements Serializable {

    @Schema(description = "")
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
