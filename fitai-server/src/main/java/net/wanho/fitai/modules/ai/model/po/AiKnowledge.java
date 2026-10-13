package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiKnowledge implements Serializable {

    @TableId
    private Long knowledgeId;
    private String kbName;
    private String kbCode;
    private String description;
    private Long embeddingModelId;
    private String embeddingModel;
    private Integer vectorDims;
    private String vectorEngine;
    private String milvusCollection;
    private String redisIndexName;
    private String distanceMetric;
    private Integer docCount;
    private Integer chunkCount;
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
