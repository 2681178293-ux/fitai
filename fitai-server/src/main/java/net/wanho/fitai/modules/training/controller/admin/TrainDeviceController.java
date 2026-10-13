package net.wanho.fitai.modules.training.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceUpdateDTO;
import net.wanho.fitai.modules.training.model.vo.TrainDeviceVO;
import net.wanho.fitai.modules.training.service.TrainDeviceServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminTrainDeviceController")
@RequestMapping("/admin/training/device")
@RequiredArgsConstructor
@Tag(name = "穿戴设备接口")
public class TrainDeviceController {

    private final TrainDeviceServiceI traindeviceService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<TrainDeviceVO>> findByPage(TrainDeviceQueryDTO queryDto) {
        return AjaxResult.success(traindeviceService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<TrainDeviceVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(traindeviceService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加穿戴设备")
    public AjaxResult<Void> add(@Valid @RequestBody TrainDeviceAddDTO addDto) {
        traindeviceService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改穿戴设备")
    public AjaxResult<Void> update(@Valid @RequestBody TrainDeviceUpdateDTO updateDto) {
        traindeviceService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除穿戴设备")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        traindeviceService.delete(id);
        return AjaxResult.success();
    }

}
