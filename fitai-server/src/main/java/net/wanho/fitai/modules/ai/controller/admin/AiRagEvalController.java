package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiRagEvalAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiRagEvalQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiRagEvalUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiRagEvalVO;
import net.wanho.fitai.modules.ai.service.AiRagEvalServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiRagEvalController")
@RequestMapping("/admin/ai/rag-eval")
@RequiredArgsConstructor
@Tag(name = "RAG质量评估接口")
public class AiRagEvalController {

    private final AiRagEvalServiceI airagevalService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiRagEvalVO>> findByPage(AiRagEvalQueryDTO queryDto) {
        return AjaxResult.success(airagevalService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiRagEvalVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(airagevalService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加RAG质量评估")
    public AjaxResult<Void> add(@Valid @RequestBody AiRagEvalAddDTO addDto) {
        airagevalService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改RAG质量评估")
    public AjaxResult<Void> update(@Valid @RequestBody AiRagEvalUpdateDTO updateDto) {
        airagevalService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除RAG质量评估")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        airagevalService.delete(id);
        return AjaxResult.success();
    }

}
