package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysMenu;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "SysMenu视图")
@AutoMapper(target = SysMenu.class)
public class SysMenuVO implements Serializable {
    @Schema(description = "菜单ID")
    private Long menuId;
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "父菜单ID")
    private Long parentId;
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "显示顺序")
    private Integer orderNum;
    @Schema(description = "路由地址")
    private String path;
    @Schema(description = "组件路径")
    private String component;
    @Schema(description = "路由名称")
    private String routeName;
    @Schema(description = "菜单类型")
    private String menuType;
    @Schema(description = "显示状态")
    private String visible;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "权限标识")
    private String perms;
    @Schema(description = "图标")
    private String icon;

    @Schema(description = "子菜单")
    private List<SysMenuVO> children;

}
