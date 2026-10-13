package net.wanho.fitai.modules.member.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.member.model.po.MemBadge;

import java.io.Serializable;

@Data
@Schema(description = "徽章定义视图")
@AutoMapper(target = MemBadge.class)
public class MemBadgeVO implements Serializable {

    private Long badgeId;
    private String badgeCode;
    private String badgeName;
    private String iconUrl;
    private String category;
    private String conditionType;
    private Integer pointsReward;
    private Integer orderNum;
    private String status;
}
