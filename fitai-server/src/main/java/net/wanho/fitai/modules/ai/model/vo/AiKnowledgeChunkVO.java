package net.wanho.fitai.modules.ai.model.vo;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiKnowledgeChunk;

@Data
@Schema(description = "知识库分片(Redis向量元数据)视图")
@AutoMapper(target = AiKnowledgeChunk.class)
public class AiKnowledgeChunkVO implements Serializable {

    @Schema(description = "分片ID")
    private Long chunkId;
    @Schema(description = "知识库ID")
    private Long knowledgeId;
    @Schema(description = "文档ID")
    private Long docId;
    @Schema(description = "分片序号")
    private Integer chunkIndex;
    @Schema(description = "分片文本")
    private String chunkText;
    @Schema(description = "内容SHA256")
    private String contentHash;
    @Schema(description = "Token数")
    private Integer tokenCount;
    @Schema(description = "Redis文档键 fitai:vec:{kb}:{doc}:{idx}")
    private String redisDocKey;
    @Schema(description = "Redis索引名(冗余)")
    private String redisIndexName;
    @Schema(description = "向量维度")
    private Integer vectorDims;
    @Schema(description = "距离度量")
    private String distanceMetric;
    @Schema(description = "嵌入模型")
    private String embeddingModel;
    @Schema(description = "向量引擎")
    private String vectorEngine;
    @Schema(description = "Milvus 向量主键")
    private String milvusVectorId;
    @Schema(description = "Redis TAG/Numeric过滤元数据")
    private String metadataJson;
    @Schema(description = "索引（0待写入 1已写入 2失败）")
    private String indexStatus;
}
