package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.Serializable;

@Data
public class UsrSetting implements Serializable {

    @TableId
    private Long settingId;
    private Long customerId;
    private String availableEquipmentJson;
    private Integer weeklyDays;
    private Integer sessionDurationMin;
    private String dietPreference;
    private String dietRestrictions;
    private String isVegetarian;
    private LocalTime reminderTime;
    private String pushEnabled;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
