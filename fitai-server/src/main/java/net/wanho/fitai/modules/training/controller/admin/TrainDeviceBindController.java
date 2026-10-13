package net.wanho.fitai.modules.training.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceBindAddDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceBindQueryDTO;
import net.wanho.fitai.modules.training.model.dto.TrainDeviceBindUpdateDTO;
import net.wanho.fitai.modules.training.model.vo.TrainDeviceBindVO;
import net.wanho.fitai.modules.training.service.TrainDeviceBindServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminTrainDeviceBindController")
@RequestMapping("/admin/training/device-bind")
@RequiredArgsConstructor
@Tag(name = "设备绑定接口")
public class TrainDeviceBindController {

    private final TrainDeviceBindServiceI traindevicebindService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<TrainDeviceBindVO>> findByPage(TrainDeviceBindQueryDTO queryDto) {
        return AjaxResult.success(traindevicebindService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<TrainDeviceBindVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(traindevicebindService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加设备绑定")
    public AjaxResult<Void> add(@Valid @RequestBody TrainDeviceBindAddDTO addDto) {
        traindevicebindService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改设备绑定")
    public AjaxResult<Void> update(@Valid @RequestBody TrainDeviceBindUpdateDTO updateDto) {
        traindevicebindService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除设备绑定")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        traindevicebindService.delete(id);
        return AjaxResult.success();
    }

}
