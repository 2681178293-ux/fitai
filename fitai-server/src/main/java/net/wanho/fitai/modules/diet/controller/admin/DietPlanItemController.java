package net.wanho.fitai.modules.diet.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.diet.model.dto.DietPlanItemAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanItemQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanItemUpdateDTO;
import net.wanho.fitai.modules.diet.model.vo.DietPlanItemVO;
import net.wanho.fitai.modules.diet.service.DietPlanItemServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminDietPlanItemController")
@RequestMapping("/admin/diet/plan-item")
@RequiredArgsConstructor
@Tag(name = "饮食方案明细接口")
public class DietPlanItemController {

    private final DietPlanItemServiceI dietplanitemService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<DietPlanItemVO>> findByPage(DietPlanItemQueryDTO queryDto) {
        return AjaxResult.success(dietplanitemService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<DietPlanItemVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(dietplanitemService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加饮食方案明细")
    public AjaxResult<Void> add(@Valid @RequestBody DietPlanItemAddDTO addDto) {
        dietplanitemService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改饮食方案明细")
    public AjaxResult<Void> update(@Valid @RequestBody DietPlanItemUpdateDTO updateDto) {
        dietplanitemService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除饮食方案明细")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        dietplanitemService.delete(id);
        return AjaxResult.success();
    }

}
