package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentRecordAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentRecordQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentRecordUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.OrdPaymentRecordVO;
import net.wanho.fitai.modules.mall.service.OrdPaymentRecordServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrdPaymentRecordController")
@RequestMapping("/admin/order/payment-record")
@RequiredArgsConstructor
@Tag(name = "支付流水接口")
public class OrdPaymentRecordController {

    private final OrdPaymentRecordServiceI ordpaymentrecordService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<OrdPaymentRecordVO>> findByPage(OrdPaymentRecordQueryDTO queryDto) {
        return AjaxResult.success(ordpaymentrecordService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<OrdPaymentRecordVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(ordpaymentrecordService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加支付流水")
    public AjaxResult<Void> add(@Valid @RequestBody OrdPaymentRecordAddDTO addDto) {
        ordpaymentrecordService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改支付流水")
    public AjaxResult<Void> update(@Valid @RequestBody OrdPaymentRecordUpdateDTO updateDto) {
        ordpaymentrecordService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除支付流水")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        ordpaymentrecordService.delete(id);
        return AjaxResult.success();
    }

}
