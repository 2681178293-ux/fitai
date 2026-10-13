package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiPlanReviewAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPlanReviewQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiPlanReviewUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiPlanReviewVO;
import net.wanho.fitai.modules.ai.service.AiPlanReviewServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiPlanReviewController")
@RequestMapping("/admin/ai/plan-review")
@RequiredArgsConstructor
@Tag(name = "AI方案审核接口")
public class AiPlanReviewController {

    private final AiPlanReviewServiceI aiplanreviewService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiPlanReviewVO>> findByPage(AiPlanReviewQueryDTO queryDto) {
        return AjaxResult.success(aiplanreviewService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiPlanReviewVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aiplanreviewService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI方案审核")
    public AjaxResult<Void> add(@Valid @RequestBody AiPlanReviewAddDTO addDto) {
        aiplanreviewService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI方案审核")
    public AjaxResult<Void> update(@Valid @RequestBody AiPlanReviewUpdateDTO updateDto) {
        aiplanreviewService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI方案审核")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aiplanreviewService.delete(id);
        return AjaxResult.success();
    }

}
