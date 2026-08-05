package net.wanho.fitai.modules.system.model.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysLoginLog implements Serializable {

    @TableId
    private Long logId;
    private String userName;
    private String userType;
    private String loginIp;
    private String loginLocation;
    private String browser;
    private String os;
    private String clientType;
    private String status;
    private String msg;
    private LocalDateTime loginTime;
}
