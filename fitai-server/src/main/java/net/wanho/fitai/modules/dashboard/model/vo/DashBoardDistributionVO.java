package net.wanho.fitai.modules.dashboard.model.vo;

import lombok.Data;

@Data
public class DashBoardDistributionVO {

    private String name;
    private int value;
    private ItemStyle itemStyle;

    @Data
    public static class ItemStyle {
        private String color;
    }
}
