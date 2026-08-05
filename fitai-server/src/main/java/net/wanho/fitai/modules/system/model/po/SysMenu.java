package net.wanho.fitai.modules.system.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysMenu implements Serializable {

    @TableId
    private Long menuId;
    private String menuName;
    private Long parentId;
    private String ancestors;
    private Integer orderNum;
    private String path;
    private String component;
    private String routeName;
    private String menuType;
    private String visible;
    private String status;
    private String perms;
    private String icon;
    private String delFlag;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
