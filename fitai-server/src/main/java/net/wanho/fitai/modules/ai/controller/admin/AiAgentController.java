package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiAgentAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiAgentQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiAgentUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiAgentVO;
import net.wanho.fitai.modules.ai.service.AiAgentServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiAgentController")
@RequestMapping("/admin/ai/agent")
@RequiredArgsConstructor
@Tag(name = "Agent编排接口")
public class AiAgentController {

    private final AiAgentServiceI aiagentService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiAgentVO>> findByPage(AiAgentQueryDTO queryDto) {
        return AjaxResult.success(aiagentService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiAgentVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aiagentService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加Agent编排")
    public AjaxResult<Void> add(@Valid @RequestBody AiAgentAddDTO addDto) {
        aiagentService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改Agent编排")
    public AjaxResult<Void> update(@Valid @RequestBody AiAgentUpdateDTO updateDto) {
        aiagentService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除Agent编排")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aiagentService.delete(id);
        return AjaxResult.success();
    }

}
