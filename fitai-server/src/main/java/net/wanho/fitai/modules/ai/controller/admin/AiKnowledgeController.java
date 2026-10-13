package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiKnowledgeUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiKnowledgeVO;
import net.wanho.fitai.modules.ai.service.AiKnowledgeServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiKnowledgeController")
@RequestMapping("/admin/ai/knowledge")
@RequiredArgsConstructor
@Tag(name = "知识库接口")
public class AiKnowledgeController {

    private final AiKnowledgeServiceI aiknowledgeService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiKnowledgeVO>> findByPage(AiKnowledgeQueryDTO queryDto) {
        return AjaxResult.success(aiknowledgeService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiKnowledgeVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aiknowledgeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加知识库")
    public AjaxResult<Void> add(@Valid @RequestBody AiKnowledgeAddDTO addDto) {
        aiknowledgeService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改知识库")
    public AjaxResult<Void> update(@Valid @RequestBody AiKnowledgeUpdateDTO updateDto) {
        aiknowledgeService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除知识库")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aiknowledgeService.delete(id);
        return AjaxResult.success();
    }

}
