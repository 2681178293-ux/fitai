package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiKnowledgeChunk implements Serializable {

    @TableId
    private Long chunkId;
    private Long knowledgeId;
    private Long docId;
    private Integer chunkIndex;
    private String chunkText;
    private String contentHash;
    private Integer tokenCount;
    private String redisDocKey;
    private String redisIndexName;
    private Integer vectorDims;
    private String distanceMetric;
    private String embeddingModel;
    private String vectorEngine;
    private String milvusVectorId;
    private String metadataJson;
    private String indexStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
