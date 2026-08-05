package net.wanho.fitai.modules.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 前端路由视图（管理端动态菜单）。
 */
@Data
@Schema(description = "前端路由")
public class RouterVO implements Serializable {

    @Schema(description = "路由地址")
    private String path;
    @Schema(description = "路由名称")
    private String name;
    @Schema(description = "组件路径")
    private String component;
    @Schema(description = "重定向")
    private String redirect;
    @Schema(description = "路由元信息")
    private Meta meta;
    @Schema(description = "子路由")
    private List<RouterVO> children = new ArrayList<>();

    @Data
    public static class Meta implements Serializable {
        @Schema(description = "标题")
        private String title;
        @Schema(description = "图标")
        private String icon;
        @Schema(description = "是否隐藏")
        private Boolean isHide = false;
        @Schema(description = "是否缓存")
        private Boolean isKeepAlive = true;
    }
}
