package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiChatSessionAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatSessionQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatSessionUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiChatSessionVO;
import net.wanho.fitai.modules.ai.service.AiChatSessionServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiChatSessionController")
@RequestMapping("/admin/ai/chat-session")
@RequiredArgsConstructor
@Tag(name = "AI对话会话接口")
public class AiChatSessionController {

    private final AiChatSessionServiceI aichatsessionService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiChatSessionVO>> findByPage(AiChatSessionQueryDTO queryDto) {
        return AjaxResult.success(aichatsessionService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiChatSessionVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aichatsessionService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI对话会话")
    public AjaxResult<Void> add(@Valid @RequestBody AiChatSessionAddDTO addDto) {
        aichatsessionService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI对话会话")
    public AjaxResult<Void> update(@Valid @RequestBody AiChatSessionUpdateDTO updateDto) {
        aichatsessionService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI对话会话")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aichatsessionService.delete(id);
        return AjaxResult.success();
    }

}
