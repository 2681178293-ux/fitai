package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdOrderUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.OrdOrderVO;
import net.wanho.fitai.modules.mall.service.OrdOrderServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrdOrderController")
@RequestMapping("/admin/order/order")
@RequiredArgsConstructor
@Tag(name = "商品订单接口")
public class OrdOrderController {

    private final OrdOrderServiceI ordorderService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<OrdOrderVO>> findByPage(OrdOrderQueryDTO queryDto) {
        return AjaxResult.success(ordorderService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<OrdOrderVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(ordorderService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加商品订单")
    public AjaxResult<Void> add(@Valid @RequestBody OrdOrderAddDTO addDto) {
        ordorderService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改商品订单")
    public AjaxResult<Void> update(@Valid @RequestBody OrdOrderUpdateDTO updateDto) {
        ordorderService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除商品订单")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        ordorderService.delete(id);
        return AjaxResult.success();
    }

}
