package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;

@Data
@Schema(description = "C端用户视图")
@AutoMapper(target = UsrCustomer.class)
public class UsrCustomerVO implements Serializable {

    @Schema(description = "C端用户ID")
    private Long customerId;
    @Schema(description = "昵称")
    private String nickName;
    @Schema(description = "手机号")
    private String mobile;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "性别（0男 1女 2未知）")
    private String gender;
    @Schema(description = "生日")
    private LocalDate birthday;
    @Schema(description = "身高cm(冗余最新体测)")
    private BigDecimal heightCm;
    @Schema(description = "体重kg(冗余最新体测)")
    private BigDecimal weightKg;
    @Schema(description = "体脂率%(冗余)")
    private BigDecimal bodyFatPct;
    @Schema(description = "健身目标 1减脂 2增肌 3塑形 4健康")
    private String fitnessGoal;
    @Schema(description = "训练水平 1新手 2进阶 3高级")
    private Integer fitnessLevel;
    @Schema(description = "会员等级ID(冗余)")
    private Long memberLevelId;
    @Schema(description = "会员等级名(冗余)")
    private String memberLevelName;
    @Schema(description = "成长值(会员等级)")
    private Integer growthValue;
    @Schema(description = "积分余额(冗余)")
    private Integer pointsBalance;
    @Schema(description = "注册来源")
    private String registerSource;
    @Schema(description = "最后登录")
    private LocalDateTime lastLoginTime;
    @Schema(description = "状态（0正常 1冻结）")
    private String status;
}
