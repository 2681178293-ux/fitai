package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiTrace implements Serializable {

    @TableId
    private Long traceId;
    private String traceUuid;
    private Long sessionId;
    private String conversationId;
    private Long customerId;
    private String modelCode;
    private String bizScene;
    private String agentCode;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer toolCallCount;
    private String stepsJson;
    private Integer totalLatencyMs;
    private String status;
    private String errorMsg;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
