package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class MemCheckin implements Serializable {

    @TableId
    private Long checkinId;
    private Long customerId;
    private LocalDate checkinDate;
    private Integer continuousDays;
    private Integer pointsEarned;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
