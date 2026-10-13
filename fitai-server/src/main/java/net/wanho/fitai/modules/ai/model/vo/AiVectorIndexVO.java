package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiVectorIndex;

@Data
@Schema(description = "Redis向量索引注册视图")
@AutoMapper(target = AiVectorIndex.class)
public class AiVectorIndexVO implements Serializable {

    @Schema(description = "索引ID")
    private Long indexId;
    @Schema(description = "Redis索引名")
    private String indexName;
    @Schema(description = "向量引擎")
    private String vectorEngine;
    @Schema(description = "键前缀 fitai:vec:")
    private String indexPrefix;
    @Schema(description = "Milvus collection")
    private String milvusCollection;
    @Schema(description = "关联知识库")
    private Long knowledgeId;
    @Schema(description = "向量维度")
    private Integer vectorDims;
    @Schema(description = "COSINE/L2/IP")
    private String distanceMetric;
    @Schema(description = "索引算法 HNSW/FLAT")
    private String algorithm;
    @Schema(description = "嵌入模型")
    private String embeddingModel;
    @Schema(description = "连接地址 milvus://host:19530")
    private String endpoint;
    @Schema(description = "文档数(冗余)")
    private Integer docCount;
    @Schema(description = "最后重建时间")
    private LocalDateTime lastRebuildTime;
    @Schema(description = "状态（0正常 1重建中 2异常）")
    private String indexStatus;
}
