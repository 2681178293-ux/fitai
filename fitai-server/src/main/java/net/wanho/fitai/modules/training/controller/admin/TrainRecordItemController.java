package net.wanho.fitai.modules.training.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.training.model.dto.TrainRecordItemAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordItemQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordItemUpdateDTO;
import net.wanho.fitai.modules.training.model.vo.TrainRecordItemVO;
import net.wanho.fitai.modules.training.service.TrainRecordItemServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminTrainRecordItemController")
@RequestMapping("/admin/training/record-item")
@RequiredArgsConstructor
@Tag(name = "训练记录明细接口")
public class TrainRecordItemController {

    private final TrainRecordItemServiceI trainrecorditemService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<TrainRecordItemVO>> findByPage(TrainRecordItemQueryDTO queryDto) {
        return AjaxResult.success(trainrecorditemService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<TrainRecordItemVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(trainrecorditemService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加训练记录明细")
    public AjaxResult<Void> add(@Valid @RequestBody TrainRecordItemAddDTO addDto) {
        trainrecorditemService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改训练记录明细")
    public AjaxResult<Void> update(@Valid @RequestBody TrainRecordItemUpdateDTO updateDto) {
        trainrecorditemService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除训练记录明细")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        trainrecorditemService.delete(id);
        return AjaxResult.success();
    }

}
