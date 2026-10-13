package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsTraceAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsTraceQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsTraceUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.OrdLogisticsTraceVO;
import net.wanho.fitai.modules.mall.service.OrdLogisticsTraceServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrdLogisticsTraceController")
@RequestMapping("/admin/order/logistics-trace")
@RequiredArgsConstructor
@Tag(name = "物流轨迹接口")
public class OrdLogisticsTraceController {

    private final OrdLogisticsTraceServiceI ordlogisticstraceService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<OrdLogisticsTraceVO>> findByPage(OrdLogisticsTraceQueryDTO queryDto) {
        return AjaxResult.success(ordlogisticstraceService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<OrdLogisticsTraceVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(ordlogisticstraceService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加物流轨迹")
    public AjaxResult<Void> add(@Valid @RequestBody OrdLogisticsTraceAddDTO addDto) {
        ordlogisticstraceService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改物流轨迹")
    public AjaxResult<Void> update(@Valid @RequestBody OrdLogisticsTraceUpdateDTO updateDto) {
        ordlogisticstraceService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除物流轨迹")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        ordlogisticstraceService.delete(id);
        return AjaxResult.success();
    }

}
