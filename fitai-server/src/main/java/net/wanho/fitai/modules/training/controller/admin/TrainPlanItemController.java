package net.wanho.fitai.modules.training.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.training.model.dto.TrainPlanItemAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanItemQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanItemUpdateDTO;
import net.wanho.fitai.modules.training.model.vo.TrainPlanItemVO;
import net.wanho.fitai.modules.training.service.TrainPlanItemServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminTrainPlanItemController")
@RequestMapping("/admin/training/plan-item")
@RequiredArgsConstructor
@Tag(name = "训练计划明细接口")
public class TrainPlanItemController {

    private final TrainPlanItemServiceI trainplanitemService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<TrainPlanItemVO>> findByPage(TrainPlanItemQueryDTO queryDto) {
        return AjaxResult.success(trainplanitemService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<TrainPlanItemVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(trainplanitemService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加训练计划明细")
    public AjaxResult<Void> add(@Valid @RequestBody TrainPlanItemAddDTO addDto) {
        trainplanitemService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改训练计划明细")
    public AjaxResult<Void> update(@Valid @RequestBody TrainPlanItemUpdateDTO updateDto) {
        trainplanitemService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除训练计划明细")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        trainplanitemService.delete(id);
        return AjaxResult.success();
    }

}
