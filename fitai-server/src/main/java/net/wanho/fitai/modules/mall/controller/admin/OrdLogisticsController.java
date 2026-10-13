package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLogisticsUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.OrdLogisticsVO;
import net.wanho.fitai.modules.mall.service.OrdLogisticsServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrdLogisticsController")
@RequestMapping("/admin/order/logistics")
@RequiredArgsConstructor
@Tag(name = "物流接口")
public class OrdLogisticsController {

    private final OrdLogisticsServiceI ordlogisticsService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<OrdLogisticsVO>> findByPage(OrdLogisticsQueryDTO queryDto) {
        return AjaxResult.success(ordlogisticsService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<OrdLogisticsVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(ordlogisticsService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加物流")
    public AjaxResult<Void> add(@Valid @RequestBody OrdLogisticsAddDTO addDto) {
        ordlogisticsService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改物流")
    public AjaxResult<Void> update(@Valid @RequestBody OrdLogisticsUpdateDTO updateDto) {
        ordlogisticsService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除物流")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        ordlogisticsService.delete(id);
        return AjaxResult.success();
    }

}
