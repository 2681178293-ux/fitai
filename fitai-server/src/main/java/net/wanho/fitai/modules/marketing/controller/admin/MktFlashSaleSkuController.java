package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleSkuAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleSkuQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleSkuUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.MktFlashSaleSkuVO;
import net.wanho.fitai.modules.marketing.service.MktFlashSaleSkuServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMktFlashSaleSkuController")
@RequestMapping("/admin/marketing/flash-sale-sku")
@RequiredArgsConstructor
@Tag(name = "秒杀SKU接口")
public class MktFlashSaleSkuController {

    private final MktFlashSaleSkuServiceI mktflashsaleskuService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MktFlashSaleSkuVO>> findByPage(MktFlashSaleSkuQueryDTO queryDto) {
        return AjaxResult.success(mktflashsaleskuService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MktFlashSaleSkuVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mktflashsaleskuService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加秒杀SKU")
    public AjaxResult<Void> add(@Valid @RequestBody MktFlashSaleSkuAddDTO addDto) {
        mktflashsaleskuService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改秒杀SKU")
    public AjaxResult<Void> update(@Valid @RequestBody MktFlashSaleSkuUpdateDTO updateDto) {
        mktflashsaleskuService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除秒杀SKU")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mktflashsaleskuService.delete(id);
        return AjaxResult.success();
    }

}
