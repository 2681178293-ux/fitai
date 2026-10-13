package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiModelAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiModelQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiModelUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiModelVO;
import net.wanho.fitai.modules.ai.service.AiModelServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiModelController")
@RequestMapping("/admin/ai/model")
@RequiredArgsConstructor
@Tag(name = "AI模型配置接口")
public class AiModelController {

    private final AiModelServiceI aimodelService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiModelVO>> findByPage(AiModelQueryDTO queryDto) {
        return AjaxResult.success(aimodelService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiModelVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aimodelService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI模型配置")
    public AjaxResult<Void> add(@Valid @RequestBody AiModelAddDTO addDto) {
        aimodelService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI模型配置")
    public AjaxResult<Void> update(@Valid @RequestBody AiModelUpdateDTO updateDto) {
        aimodelService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI模型配置")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aimodelService.delete(id);
        return AjaxResult.success();
    }

}
