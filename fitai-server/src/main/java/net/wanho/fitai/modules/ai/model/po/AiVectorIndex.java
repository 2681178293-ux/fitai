package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiVectorIndex implements Serializable {

    @TableId
    private Long indexId;
    private String indexName;
    private String vectorEngine;
    private String indexPrefix;
    private String milvusCollection;
    private Long knowledgeId;
    private Integer vectorDims;
    private String distanceMetric;
    private String algorithm;
    private String embeddingModel;
    private String endpoint;
    private Integer docCount;
    private LocalDateTime lastRebuildTime;
    private String indexStatus;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
