package net.wanho.fitai.modules.system.model.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysOperLog;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Schema(description = "SysOperLog视图")
@AutoMapper(target = SysOperLog.class)
public class SysOperLogVO implements Serializable {
    @Schema(description = "日志主键")
    private Long operId;
    @Schema(description = "模块标题")
    private String title;
    @Schema(description = "业务类型")
    private Integer businessType;
    @Schema(description = "方法名称")
    private String method;
    @Schema(description = "请求方式")
    private String requestMethod;
    @Schema(description = "操作人员")
    private String operName;
    @Schema(description = "请求URL")
    private String operUrl;
    @Schema(description = "主机地址")
    private String operIp;
    @Schema(description = "请求参数")
    private String operParam;
    @Schema(description = "返回参数")
    private String jsonResult;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "错误消息")
    private String errorMsg;
    @Schema(description = "操作时间")
    private LocalDateTime operTime;
    @Schema(description = "消耗时间(毫秒)")
    private Long costTime;

}
