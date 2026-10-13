package net.wanho.fitai.modules.training.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.training.model.dto.TrainRecordAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainRecordUpdateDTO;
import net.wanho.fitai.modules.training.model.vo.TrainRecordVO;
import net.wanho.fitai.modules.training.service.TrainRecordServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminTrainRecordController")
@RequestMapping("/admin/training/record")
@RequiredArgsConstructor
@Tag(name = "训练记录接口")
public class TrainRecordController {

    private final TrainRecordServiceI trainrecordService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<TrainRecordVO>> findByPage(TrainRecordQueryDTO queryDto) {
        return AjaxResult.success(trainrecordService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<TrainRecordVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(trainrecordService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加训练记录")
    public AjaxResult<Void> add(@Valid @RequestBody TrainRecordAddDTO addDto) {
        trainrecordService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改训练记录")
    public AjaxResult<Void> update(@Valid @RequestBody TrainRecordUpdateDTO updateDto) {
        trainrecordService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除训练记录")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        trainrecordService.delete(id);
        return AjaxResult.success();
    }

}
