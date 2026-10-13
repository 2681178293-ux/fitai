package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalTime;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrSetting;

@Data
@Schema(description = "用户训练/饮食偏好设置视图")
@AutoMapper(target = UsrSetting.class)
public class UsrSettingVO implements Serializable {

    @Schema(description = "设置ID")
    private Long settingId;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "可用器材列表")
    private String availableEquipmentJson;
    @Schema(description = "每周训练天数")
    private Integer weeklyDays;
    @Schema(description = "单次训练时长(分钟)")
    private Integer sessionDurationMin;
    @Schema(description = "口味偏好")
    private String dietPreference;
    @Schema(description = "忌口")
    private String dietRestrictions;
    @Schema(description = "是否素食 0否 1是")
    private String isVegetarian;
    @Schema(description = "训练提醒时间")
    private LocalTime reminderTime;
    @Schema(description = "推送开关 0关 1开")
    private String pushEnabled;
}
