package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiChatMessageAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatMessageQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiChatMessageUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiChatMessageVO;
import net.wanho.fitai.modules.ai.service.AiChatMessageServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiChatMessageController")
@RequestMapping("/admin/ai/chat-message")
@RequiredArgsConstructor
@Tag(name = "AI对话消息接口")
public class AiChatMessageController {

    private final AiChatMessageServiceI aichatmessageService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiChatMessageVO>> findByPage(AiChatMessageQueryDTO queryDto) {
        return AjaxResult.success(aichatmessageService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiChatMessageVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aichatmessageService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI对话消息")
    public AjaxResult<Void> add(@Valid @RequestBody AiChatMessageAddDTO addDto) {
        aichatmessageService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI对话消息")
    public AjaxResult<Void> update(@Valid @RequestBody AiChatMessageUpdateDTO updateDto) {
        aichatmessageService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI对话消息")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aichatmessageService.delete(id);
        return AjaxResult.success();
    }

}
