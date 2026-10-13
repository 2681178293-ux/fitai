package net.wanho.fitai.modules.training.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.training.model.dto.TrainActionAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainActionQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainActionUpdateDTO;
import net.wanho.fitai.modules.training.model.vo.TrainActionVO;
import net.wanho.fitai.modules.training.service.TrainActionServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminTrainActionController")
@RequestMapping("/admin/exercise/action")
@RequiredArgsConstructor
@Tag(name = "动作库接口")
public class TrainActionController {

    private final TrainActionServiceI trainactionService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<TrainActionVO>> findByPage(TrainActionQueryDTO queryDto) {
        return AjaxResult.success(trainactionService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<TrainActionVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(trainactionService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加动作库")
    public AjaxResult<Void> add(@Valid @RequestBody TrainActionAddDTO addDto) {
        trainactionService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改动作库")
    public AjaxResult<Void> update(@Valid @RequestBody TrainActionUpdateDTO updateDto) {
        trainactionService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除动作库")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        trainactionService.delete(id);
        return AjaxResult.success();
    }

}
