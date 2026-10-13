package net.wanho.fitai.modules.training.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.training.model.dto.TrainPlanAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainPlanUpdateDTO;
import net.wanho.fitai.modules.training.model.vo.TrainPlanVO;
import net.wanho.fitai.modules.training.service.TrainPlanServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminTrainPlanController")
@RequestMapping("/admin/training/plan")
@RequiredArgsConstructor
@Tag(name = "训练计划接口")
public class TrainPlanController {

    private final TrainPlanServiceI trainplanService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<TrainPlanVO>> findByPage(TrainPlanQueryDTO queryDto) {
        return AjaxResult.success(trainplanService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<TrainPlanVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(trainplanService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加训练计划")
    public AjaxResult<Void> add(@Valid @RequestBody TrainPlanAddDTO addDto) {
        trainplanService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改训练计划")
    public AjaxResult<Void> update(@Valid @RequestBody TrainPlanUpdateDTO updateDto) {
        trainplanService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除训练计划")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        trainplanService.delete(id);
        return AjaxResult.success();
    }

}
