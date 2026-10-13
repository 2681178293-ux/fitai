package net.wanho.fitai.modules.ai.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI非会话Token消耗(审计/统计)查询参数")
public class AiTokenUsageQueryDTO extends PageQuery {

    @Schema(description = "模型编码")
    private String modelCode;
}
