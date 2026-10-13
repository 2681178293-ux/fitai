package net.wanho.fitai.modules.coach.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.coach.model.po.UsrCoachBind;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户教练绑定视图")
@AutoMapper(target = UsrCoachBind.class)
public class UsrCoachBindVO implements Serializable {

    private Long bindId;
    private Long customerId;
    private Long coachId;
    private String coachName;
    private String bindType;
    private String status;
    private String startTime;
    private String endTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
