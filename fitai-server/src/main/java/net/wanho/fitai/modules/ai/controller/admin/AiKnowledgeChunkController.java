package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeChunkAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeChunkQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeChunkUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiKnowledgeChunkVO;
import net.wanho.fitai.modules.ai.service.AiKnowledgeChunkServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiKnowledgeChunkController")
@RequestMapping("/admin/ai/knowledge-chunk")
@RequiredArgsConstructor
@Tag(name = "知识库分片(Redis向量元数据)接口")
public class AiKnowledgeChunkController {

    private final AiKnowledgeChunkServiceI aiknowledgechunkService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiKnowledgeChunkVO>> findByPage(AiKnowledgeChunkQueryDTO queryDto) {
        return AjaxResult.success(aiknowledgechunkService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiKnowledgeChunkVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aiknowledgechunkService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加知识库分片(Redis向量元数据)")
    public AjaxResult<Void> add(@Valid @RequestBody AiKnowledgeChunkAddDTO addDto) {
        aiknowledgechunkService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改知识库分片(Redis向量元数据)")
    public AjaxResult<Void> update(@Valid @RequestBody AiKnowledgeChunkUpdateDTO updateDto) {
        aiknowledgechunkService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除知识库分片(Redis向量元数据)")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aiknowledgechunkService.delete(id);
        return AjaxResult.success();
    }

}
