package net.wanho.fitai.modules.course.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrsLearnProgressStatVO {

    private Long courseId;
    private String courseTitle;
    private long totalLearners;
    private long completedCount;
    private BigDecimal completionRate;
}