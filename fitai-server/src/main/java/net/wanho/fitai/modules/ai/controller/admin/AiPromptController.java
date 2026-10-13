package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiPromptAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPromptQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPromptUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiPromptVO;
import net.wanho.fitai.modules.ai.service.AiPromptServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiPromptController")
@RequestMapping("/admin/ai/prompt")
@RequiredArgsConstructor
@Tag(name = "提示词管理接口")
public class AiPromptController {

    private final AiPromptServiceI aipromptService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiPromptVO>> findByPage(AiPromptQueryDTO queryDto) {
        return AjaxResult.success(aipromptService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiPromptVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aipromptService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加提示词管理")
    public AjaxResult<Void> add(@Valid @RequestBody AiPromptAddDTO addDto) {
        aipromptService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改提示词管理")
    public AjaxResult<Void> update(@Valid @RequestBody AiPromptUpdateDTO updateDto) {
        aipromptService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除提示词管理")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aipromptService.delete(id);
        return AjaxResult.success();
    }

}
