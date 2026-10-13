package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiTokenUsageAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTokenUsageQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiTokenUsageUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiTokenUsageVO;
import net.wanho.fitai.modules.ai.service.AiTokenUsageServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiTokenUsageController")
@RequestMapping("/admin/ai/token-usage")
@RequiredArgsConstructor
@Tag(name = "AI非会话Token消耗(审计/统计)接口")
public class AiTokenUsageController {

    private final AiTokenUsageServiceI aitokenusageService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiTokenUsageVO>> findByPage(AiTokenUsageQueryDTO queryDto) {
        return AjaxResult.success(aitokenusageService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiTokenUsageVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aitokenusageService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI非会话Token消耗(审计/统计)")
    public AjaxResult<Void> add(@Valid @RequestBody AiTokenUsageAddDTO addDto) {
        aitokenusageService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI非会话Token消耗(审计/统计)")
    public AjaxResult<Void> update(@Valid @RequestBody AiTokenUsageUpdateDTO updateDto) {
        aitokenusageService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI非会话Token消耗(审计/统计)")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aitokenusageService.delete(id);
        return AjaxResult.success();
    }

}
