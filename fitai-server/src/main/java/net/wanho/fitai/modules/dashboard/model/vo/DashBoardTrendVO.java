package net.wanho.fitai.modules.dashboard.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashBoardTrendVO {

    private List<String> dates;
    private List<Long> orderCounts;
    private List<Double> salesAmounts;
}
