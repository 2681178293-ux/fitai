package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.SeckillSkuAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillSkuQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillSkuUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.SeckillSkuVO;
import net.wanho.fitai.modules.marketing.service.SeckillSkuServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSeckillSkuController")
@RequestMapping("/admin/seckill/sku")
@RequiredArgsConstructor
@Tag(name = "秒杀商品表接口")
public class SeckillSkuController {

    private final SeckillSkuServiceI seckillskuService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<SeckillSkuVO>> findByPage(SeckillSkuQueryDTO queryDto) {
        return AjaxResult.success(seckillskuService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<SeckillSkuVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(seckillskuService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加秒杀商品表")
    public AjaxResult<Void> add(@Valid @RequestBody SeckillSkuAddDTO addDto) {
        seckillskuService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改秒杀商品表")
    public AjaxResult<Void> update(@Valid @RequestBody SeckillSkuUpdateDTO updateDto) {
        seckillskuService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除秒杀商品表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        seckillskuService.delete(id);
        return AjaxResult.success();
    }

}
