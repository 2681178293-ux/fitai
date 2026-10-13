package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiKnowledgeChunk;

@Data
@Schema(description = "新增知识库分片(Redis向量元数据)")
@AutoMapper(target = AiKnowledgeChunk.class)
public class AiKnowledgeChunkAddDTO implements Serializable {

    @Schema(description = "知识库ID")
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeId;
    @Schema(description = "文档ID")
    @NotNull(message = "文档ID不能为空")
    private Long docId;
    @Schema(description = "分片序号")
    @NotNull(message = "分片序号不能为空")
    private Integer chunkIndex;
    @Schema(description = "分片文本")
    @NotBlank(message = "分片文本不能为空")
    private String chunkText;
    @Schema(description = "内容SHA256")
    @NotBlank(message = "内容SHA256不能为空")
    private String contentHash;
    @Schema(description = "Token数")
    private Integer tokenCount;
    @Schema(description = "Redis文档键 fitai:vec:{kb}:{doc}:{idx}")
    @NotBlank(message = "Redis文档键 fitai不能为空")
    private String redisDocKey;
    @Schema(description = "Redis索引名(冗余)")
    @NotBlank(message = "Redis索引名不能为空")
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
