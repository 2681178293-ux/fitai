package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdStockAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdStockQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdStockUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdStockVO;
import net.wanho.fitai.modules.mall.service.PrdStockServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdStockController")
@RequestMapping("/admin/product/stock")
@RequiredArgsConstructor
@Tag(name = "SKU库存接口")
public class PrdStockController {

    private final PrdStockServiceI prdstockService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdStockVO>> findByPage(PrdStockQueryDTO queryDto) {
        return AjaxResult.success(prdstockService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdStockVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdstockService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加SKU库存")
    public AjaxResult<Void> add(@Valid @RequestBody PrdStockAddDTO addDto) {
        prdstockService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改SKU库存")
    public AjaxResult<Void> update(@Valid @RequestBody PrdStockUpdateDTO updateDto) {
        prdstockService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除SKU库存")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdstockService.delete(id);
        return AjaxResult.success();
    }

}
