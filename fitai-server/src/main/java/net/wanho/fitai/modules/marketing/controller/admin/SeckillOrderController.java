package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.SeckillOrderAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillOrderQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillOrderUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.SeckillOrderVO;
import net.wanho.fitai.modules.marketing.service.SeckillOrderServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSeckillOrderController")
@RequestMapping("/admin/seckill/order")
@RequiredArgsConstructor
@Tag(name = "秒杀订单表接口")
public class SeckillOrderController {

    private final SeckillOrderServiceI seckillorderService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<SeckillOrderVO>> findByPage(SeckillOrderQueryDTO queryDto) {
        return AjaxResult.success(seckillorderService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<SeckillOrderVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(seckillorderService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加秒杀订单表")
    public AjaxResult<Void> add(@Valid @RequestBody SeckillOrderAddDTO addDto) {
        seckillorderService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改秒杀订单表")
    public AjaxResult<Void> update(@Valid @RequestBody SeckillOrderUpdateDTO updateDto) {
        seckillorderService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除秒杀订单表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        seckillorderService.delete(id);
        return AjaxResult.success();
    }

}
