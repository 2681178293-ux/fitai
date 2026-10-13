package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktFlashSale;

@Data
@Schema(description = "秒杀活动修改参数")
@AutoMapper(target = MktFlashSale.class)
public class MktFlashSaleUpdateDTO implements Serializable {

    @Schema(description = "活动ID")
    @NotNull(message = "活动ID不能为空")
    private Long activityId;
    @Schema(description = "活动名称")
    private String activityName;
    @Schema(description = "开始")
    private LocalDateTime startTime;
    @Schema(description = "结束")
    private LocalDateTime endTime;
    @Schema(description = "状态（0未开始 1进行中 2已结束）")
    private String status;
    @Schema(description = "单人限购件数，0表示不限")
    private Integer limitPerUser;
}
