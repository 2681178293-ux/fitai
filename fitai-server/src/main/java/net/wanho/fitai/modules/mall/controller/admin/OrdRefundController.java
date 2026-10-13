package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.OrdRefundAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdRefundQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdRefundUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.OrdRefundVO;
import net.wanho.fitai.modules.mall.service.OrdRefundServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrdRefundController")
@RequestMapping("/admin/order/refund")
@RequiredArgsConstructor
@Tag(name = "售后退款接口")
public class OrdRefundController {

    private final OrdRefundServiceI ordrefundService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<OrdRefundVO>> findByPage(OrdRefundQueryDTO queryDto) {
        return AjaxResult.success(ordrefundService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<OrdRefundVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(ordrefundService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加售后退款")
    public AjaxResult<Void> add(@Valid @RequestBody OrdRefundAddDTO addDto) {
        ordrefundService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改售后退款")
    public AjaxResult<Void> update(@Valid @RequestBody OrdRefundUpdateDTO updateDto) {
        ordrefundService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除售后退款")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        ordrefundService.delete(id);
        return AjaxResult.success();
    }

}
