package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiCostLogAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiCostLogQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiCostLogUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiCostLogVO;
import net.wanho.fitai.modules.ai.service.AiCostLogServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiCostLogController")
@RequestMapping("/admin/ai/cost-log")
@RequiredArgsConstructor
@Tag(name = "AI成本流水接口")
public class AiCostLogController {

    private final AiCostLogServiceI aicostlogService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiCostLogVO>> findByPage(AiCostLogQueryDTO queryDto) {
        return AjaxResult.success(aicostlogService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiCostLogVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aicostlogService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI成本流水")
    public AjaxResult<Void> add(@Valid @RequestBody AiCostLogAddDTO addDto) {
        aicostlogService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI成本流水")
    public AjaxResult<Void> update(@Valid @RequestBody AiCostLogUpdateDTO updateDto) {
        aicostlogService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI成本流水")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aicostlogService.delete(id);
        return AjaxResult.success();
    }

}
