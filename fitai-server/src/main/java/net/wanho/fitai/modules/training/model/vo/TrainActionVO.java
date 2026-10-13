package net.wanho.fitai.modules.training.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import net.wanho.fitai.modules.training.model.po.TrainAction;

@Data
@Schema(description = "动作库视图")
@AutoMapper(target = TrainAction.class)
public class TrainActionVO implements Serializable {

    @Schema(description = "动作ID")
    private Long actionId;
    @Schema(description = "动作名称")
    private String actionName;
    @Schema(description = "动作编码")
    private String actionCode;
    @Schema(description = "肌群")
    private String muscleGroup;
    @Schema(description = "器械")
    private String equipment;
    @Schema(description = "难度 1入门 2进阶 3高级")
    private Integer difficulty;
    @Schema(description = "每分钟消耗kcal")
    private BigDecimal caloriesPerMin;
    @Schema(description = "动作要点")
    private String tips;
    @Schema(description = "常见错误")
    private String commonMistakes;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "示范视频ID(media_vod)")
    private Long vodAssetId;
    @Schema(description = "VOD VideoId或播放URL(冗余，管理端videoUrl)")
    private String vodVideoId;
    @Schema(description = "封面(冗余)")
    private String vodCoverUrl;
    @Schema(description = "时长(冗余)")
    private BigDecimal vodDuration;
    @Schema(description = "说明")
    private String description;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
