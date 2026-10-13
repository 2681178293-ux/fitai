package net.wanho.fitai.modules.diet.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.diet.model.dto.DietPlanAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietPlanUpdateDTO;
import net.wanho.fitai.modules.diet.model.vo.DietPlanVO;
import net.wanho.fitai.modules.diet.service.DietPlanServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminDietPlanController")
@RequestMapping("/admin/diet/plan")
@RequiredArgsConstructor
@Tag(name = "饮食方案接口")
public class DietPlanController {

    private final DietPlanServiceI dietplanService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<DietPlanVO>> findByPage(DietPlanQueryDTO queryDto) {
        return AjaxResult.success(dietplanService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<DietPlanVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(dietplanService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加饮食方案")
    public AjaxResult<Void> add(@Valid @RequestBody DietPlanAddDTO addDto) {
        dietplanService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改饮食方案")
    public AjaxResult<Void> update(@Valid @RequestBody DietPlanUpdateDTO updateDto) {
        dietplanService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除饮食方案")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        dietplanService.delete(id);
        return AjaxResult.success();
    }

}
