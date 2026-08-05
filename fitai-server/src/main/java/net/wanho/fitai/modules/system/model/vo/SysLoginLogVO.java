package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysLoginLog;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Schema(description = "SysLoginLog视图")
@AutoMapper(target = SysLoginLog.class)
public class SysLoginLogVO implements Serializable {
    @Schema(description = "日志ID")
    private Long logId;
    @Schema(description = "登录账号")
    private String userName;
    @Schema(description = "用户类型")
    private String userType;
    @Schema(description = "登录IP")
    private String loginIp;
    @Schema(description = "登录地点")
    private String loginLocation;
    @Schema(description = "浏览器")
    private String browser;
    @Schema(description = "操作系统")
    private String os;
    @Schema(description = "客户端")
    private String clientType;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "提示消息")
    private String msg;
    @Schema(description = "登录时间")
    private LocalDateTime loginTime;

}
