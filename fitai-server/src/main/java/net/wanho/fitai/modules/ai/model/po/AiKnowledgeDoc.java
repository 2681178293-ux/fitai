package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiKnowledgeDoc implements Serializable {

    @TableId
    private Long docId;
    private Long knowledgeId;
    private String docTitle;
    private String docType;
    private String category;
    private String tags;
    private String source;
    private String sourceUrl;
    private String contentText;
    private String contentHash;
    private Integer chunkCount;
    private String indexStatus;
    private String status;
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
