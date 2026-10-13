package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdSkuVO;
import net.wanho.fitai.modules.mall.service.PrdSkuServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdSkuController")
@RequestMapping("/admin/product/sku")
@RequiredArgsConstructor
@Tag(name = "商品SKU接口")
public class PrdSkuController {

    private final PrdSkuServiceI prdskuService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdSkuVO>> findByPage(PrdSkuQueryDTO queryDto) {
        return AjaxResult.success(prdskuService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdSkuVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdskuService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加商品SKU")
    public AjaxResult<Void> add(@Valid @RequestBody PrdSkuAddDTO addDto) {
        prdskuService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改商品SKU")
    public AjaxResult<Void> update(@Valid @RequestBody PrdSkuUpdateDTO updateDto) {
        prdskuService.update(updateDto);
        return AjaxResult.success();
    }

//    @DeleteMapping("/{id}")
//    @Operation(summary = "删除")
//    @OperLog(title = "删除商品SKU")
//    public AjaxResult<Void> delete(@PathVariable("id") String id) {
//        prdskuService.delete(id);
//        return AjaxResult.success();
//    }

}
