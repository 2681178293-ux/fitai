package net.wanho.fitai.modules.ai.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import net.wanho.fitai.modules.ai.model.po.AiKnowledgeDoc;

@Data
@Schema(description = "新增知识库文档")
@AutoMapper(target = AiKnowledgeDoc.class)
public class AiKnowledgeDocAddDTO implements Serializable {

    @Schema(description = "知识库ID")
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeId;
    @Schema(description = "文档标题")
    @NotBlank(message = "文档标题不能为空")
    private String docTitle;
    @Schema(description = "类型 text/pdf/md/url")
    private String docType;
    @Schema(description = "分类 exercise/nutrition/injury/equipment")
    private String category;
    @Schema(description = "标签(逗号分隔)")
    private String tags;
    @Schema(description = "来源")
    private String source;
    @Schema(description = "来源URL")
    private String sourceUrl;
    @Schema(description = "原文")
    private String contentText;
    @Schema(description = "全文SHA256")
    private String contentHash;
    @Schema(description = "分片数(冗余)")
    private Integer chunkCount;
    @Schema(description = "索引（0待处理 1已完成 2失败）")
    private String indexStatus;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
}
