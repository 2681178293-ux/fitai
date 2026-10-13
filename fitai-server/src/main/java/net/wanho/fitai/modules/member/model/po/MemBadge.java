package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MemBadge implements Serializable {

    @TableId
    private Long badgeId;
    private String badgeCode;
    private String badgeName;
    private String description;
    private String iconUrl;
    private String category;
    private String conditionType;
    private Integer conditionValue;
    private Integer pointsReward;
    private Integer orderNum;
    private String status;
    private String delFlag;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
}
