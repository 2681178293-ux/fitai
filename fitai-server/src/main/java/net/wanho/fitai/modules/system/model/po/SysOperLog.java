package net.wanho.fitai.modules.system.model.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysOperLog implements Serializable {

    @TableId
    private Long operId;
    private String title;
    private Integer businessType;
    private String method;
    private String requestMethod;
    private String operName;
    private String operUrl;
    private String operIp;
    private String operParam;
    private String jsonResult;
    private String status;
    private String errorMsg;
    private LocalDateTime operTime;
    private Long costTime;
}
