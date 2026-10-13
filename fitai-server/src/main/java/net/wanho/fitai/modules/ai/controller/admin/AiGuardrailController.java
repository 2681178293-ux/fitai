package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiGuardrailAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiGuardrailQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiGuardrailUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiGuardrailVO;
import net.wanho.fitai.modules.ai.service.AiGuardrailServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiGuardrailController")
@RequestMapping("/admin/ai/guardrail")
@RequiredArgsConstructor
@Tag(name = "内容护栏接口")
public class AiGuardrailController {

    private final AiGuardrailServiceI aiguardrailService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiGuardrailVO>> findByPage(AiGuardrailQueryDTO queryDto) {
        return AjaxResult.success(aiguardrailService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiGuardrailVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aiguardrailService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加内容护栏")
    public AjaxResult<Void> add(@Valid @RequestBody AiGuardrailAddDTO addDto) {
        aiguardrailService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改内容护栏")
    public AjaxResult<Void> update(@Valid @RequestBody AiGuardrailUpdateDTO updateDto) {
        aiguardrailService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除内容护栏")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aiguardrailService.delete(id);
        return AjaxResult.success();
    }

}
