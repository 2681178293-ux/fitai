package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiTraceAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTraceQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTraceUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiTraceVO;
import net.wanho.fitai.modules.ai.service.AiTraceServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiTraceController")
@RequestMapping("/admin/ai/trace")
@RequiredArgsConstructor
@Tag(name = "AI链路追踪接口")
public class AiTraceController {

    private final AiTraceServiceI aitraceService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiTraceVO>> findByPage(AiTraceQueryDTO queryDto) {
        return AjaxResult.success(aitraceService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiTraceVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aitraceService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI链路追踪")
    public AjaxResult<Void> add(@Valid @RequestBody AiTraceAddDTO addDto) {
        aitraceService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI链路追踪")
    public AjaxResult<Void> update(@Valid @RequestBody AiTraceUpdateDTO updateDto) {
        aitraceService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI链路追踪")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aitraceService.delete(id);
        return AjaxResult.success();
    }

}
