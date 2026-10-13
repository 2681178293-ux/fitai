package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiKnowledge;

@Data
@Schema(description = "知识库修改参数")
@AutoMapper(target = AiKnowledge.class)
public class AiKnowledgeUpdateDTO implements Serializable {

    @Schema(description = "知识库ID")
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeId;
    @Schema(description = "知识库名称")
    private String kbName;
    @Schema(description = "编码")
    private String kbCode;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "嵌入模型ID")
    private Long embeddingModelId;
    @Schema(description = "DashScope嵌入模型")
    private String embeddingModel;
    @Schema(description = "向量维度")
    private Integer vectorDims;
    @Schema(description = "向量引擎 milvus/redis")
    private String vectorEngine;
    @Schema(description = "Milvus collection 名")
    private String milvusCollection;
    @Schema(description = "Redis索引名 idx:fitai:kb:{code}")
    private String redisIndexName;
    @Schema(description = "距离度量 COSINE/L2/IP")
    private String distanceMetric;
    @Schema(description = "文档数(冗余)")
    private Integer docCount;
    @Schema(description = "分片数(冗余)")
    private Integer chunkCount;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
