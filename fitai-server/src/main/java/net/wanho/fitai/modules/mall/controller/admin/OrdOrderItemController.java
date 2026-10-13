package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderItemAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderItemQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderItemUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.OrdOrderItemVO;
import net.wanho.fitai.modules.mall.service.OrdOrderItemServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrdOrderItemController")
@RequestMapping("/admin/order/order-item")
@RequiredArgsConstructor
@Tag(name = "订单明细(SKU快照)接口")
public class OrdOrderItemController {

    private final OrdOrderItemServiceI ordorderitemService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<OrdOrderItemVO>> findByPage(OrdOrderItemQueryDTO queryDto) {
        return AjaxResult.success(ordorderitemService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<OrdOrderItemVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(ordorderitemService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加订单明细(SKU快照)")
    public AjaxResult<Void> add(@Valid @RequestBody OrdOrderItemAddDTO addDto) {
        ordorderitemService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改订单明细(SKU快照)")
    public AjaxResult<Void> update(@Valid @RequestBody OrdOrderItemUpdateDTO updateDto) {
        ordorderitemService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除订单明细(SKU快照)")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        ordorderitemService.delete(id);
        return AjaxResult.success();
    }

}
