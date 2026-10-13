package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeDocAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeDocQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeDocUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiKnowledgeDocVO;
import net.wanho.fitai.modules.ai.service.AiKnowledgeDocServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiKnowledgeDocController")
@RequestMapping("/admin/ai/knowledge-doc")
@RequiredArgsConstructor
@Tag(name = "知识库文档接口")
public class AiKnowledgeDocController {

    private final AiKnowledgeDocServiceI aiknowledgedocService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiKnowledgeDocVO>> findByPage(AiKnowledgeDocQueryDTO queryDto) {
        return AjaxResult.success(aiknowledgedocService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiKnowledgeDocVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aiknowledgedocService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加知识库文档")
    public AjaxResult<Void> add(@Valid @RequestBody AiKnowledgeDocAddDTO addDto) {
        aiknowledgedocService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改知识库文档")
    public AjaxResult<Void> update(@Valid @RequestBody AiKnowledgeDocUpdateDTO updateDto) {
        aiknowledgedocService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除知识库文档")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aiknowledgedocService.delete(id);
        return AjaxResult.success();
    }

}
