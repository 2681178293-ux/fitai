package net.wanho.fitai.modules.system.model.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysJobLog implements Serializable {

    @TableId
    private Long jobLogId;
    private Long jobId;
    private String jobName;
    private String jobGroup;
    private String invokeTarget;
    private String jobMessage;
    private String status;
    private String exceptionInfo;
    private LocalDateTime createTime;
}
