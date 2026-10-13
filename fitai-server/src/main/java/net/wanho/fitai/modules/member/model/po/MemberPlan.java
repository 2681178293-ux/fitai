package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("vip_plan")
public class MemberPlan implements Serializable {

    @TableId("plan_id")
    private Long id;
    @TableField("plan_name")
    private String name;
    /** 套餐类型（1月卡 2季卡 3年卡），库列 package_type */
    @TableField("package_type")
    private String type;
    private Integer durationDays;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String description;
    private String benefits;
    /** 每月加赠 AI Token，库列 ai_quota_monthly */
    @TableField("ai_quota_monthly")
    private Integer aiTokenQuotaMonthly;
    /** 排序，库列 order_num */
    @TableField("order_num")
    private Integer sortOrder;
    /** 状态（0上架 1下架） */
    private String status;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    /** 删除标志（0正常 2删除），对接全局逻辑删除 */
    private String delFlag;
}
