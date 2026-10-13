package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentChannelAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentChannelQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentChannelUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.OrdPaymentChannelVO;
import net.wanho.fitai.modules.mall.service.OrdPaymentChannelServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrdPaymentChannelController")
@RequestMapping("/admin/order/payment-channel")
@RequiredArgsConstructor
@Tag(name = "支付渠道接口")
public class OrdPaymentChannelController {

    private final OrdPaymentChannelServiceI ordpaymentchannelService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<OrdPaymentChannelVO>> findByPage(OrdPaymentChannelQueryDTO queryDto) {
        return AjaxResult.success(ordpaymentchannelService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<OrdPaymentChannelVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(ordpaymentchannelService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加支付渠道")
    public AjaxResult<Void> add(@Valid @RequestBody OrdPaymentChannelAddDTO addDto) {
        ordpaymentchannelService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改支付渠道")
    public AjaxResult<Void> update(@Valid @RequestBody OrdPaymentChannelUpdateDTO updateDto) {
        ordpaymentchannelService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除支付渠道")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        ordpaymentchannelService.delete(id);
        return AjaxResult.success();
    }

}
