package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiChatSession implements Serializable {

    @TableId
    private Long sessionId;
    private String conversationId;
    private Long customerId;
    private Long modelId;
    private String modelCode;
    private String sessionTitle;
    private String scene;
    private Integer messageCount;
    private Integer totalTokens;
    private LocalDateTime lastMessageTime;
    private String status;
    private String delFlag;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
