package net.wanho.fitai.modules.dashboard.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DashBoardActivitiesVO {

    private String content;
    private String time;
    private String color;
    private LocalDateTime ts;
}
