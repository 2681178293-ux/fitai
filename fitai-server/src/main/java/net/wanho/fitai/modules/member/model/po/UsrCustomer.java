package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class UsrCustomer implements Serializable {

    @TableId
    private Long customerId;
    private String nickName;
    private String mobile;
    private String email;
    private String password;
    private String avatar;
    private String gender;
    private LocalDate birthday;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal bodyFatPct;
    private String fitnessGoal;
    private Integer fitnessLevel;
    private Long memberLevelId;
    private String memberLevelName;
    private Integer growthValue;
    private Integer pointsBalance;
    private String registerSource;
    private LocalDateTime lastLoginTime;
    private String status;
    private String delFlag;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
