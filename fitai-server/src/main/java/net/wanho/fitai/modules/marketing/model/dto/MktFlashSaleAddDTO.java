package net.wanho.fitai.modules.marketing.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.marketing.model.po.MktFlashSale;

@Data
@Schema(description = "新增秒杀活动")
@AutoMapper(target = MktFlashSale.class)
public class MktFlashSaleAddDTO implements Serializable {

    @Schema(description = "活动名称")
    @NotBlank(message = "活动名称不能为空")
    private String activityName;
    @Schema(description = "开始")
    @NotNull(message = "开始不能为空")
    private LocalDateTime startTime;
    @Schema(description = "结束")
    @NotNull(message = "结束不能为空")
    private LocalDateTime endTime;
    @Schema(description = "状态（0未开始 1进行中 2已结束）")
    private String status;
    @Schema(description = "单人限购件数，0表示不限")
    private Integer limitPerUser;
}
