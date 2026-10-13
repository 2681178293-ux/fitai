package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiEvaluationAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiEvaluationQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiEvaluationUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiEvaluationVO;
import net.wanho.fitai.modules.ai.service.AiEvaluationServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiEvaluationController")
@RequestMapping("/admin/ai/evaluation")
@RequiredArgsConstructor
@Tag(name = "模型评测接口")
public class AiEvaluationController {

    private final AiEvaluationServiceI aievaluationService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiEvaluationVO>> findByPage(AiEvaluationQueryDTO queryDto) {
        return AjaxResult.success(aievaluationService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiEvaluationVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aievaluationService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加模型评测")
    public AjaxResult<Void> add(@Valid @RequestBody AiEvaluationAddDTO addDto) {
        aievaluationService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改模型评测")
    public AjaxResult<Void> update(@Valid @RequestBody AiEvaluationUpdateDTO updateDto) {
        aievaluationService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除模型评测")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aievaluationService.delete(id);
        return AjaxResult.success();
    }

}
