package net.wanho.fitai.modules.marketing.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktFlashSale;

@Data
@Schema(description = "秒杀活动视图")
@AutoMapper(target = MktFlashSale.class)
public class MktFlashSaleVO implements Serializable {

    @Schema(description = "活动ID")
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
