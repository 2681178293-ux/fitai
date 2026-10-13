package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAttrAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAttrQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSkuAttrUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdSkuAttrVO;
import net.wanho.fitai.modules.mall.service.PrdSkuAttrServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdSkuAttrController")
@RequestMapping("/admin/product/sku-attr")
@RequiredArgsConstructor
@Tag(name = "SKU规格关联（复合主键）接口")
public class PrdSkuAttrController {

    private final PrdSkuAttrServiceI prdskuattrService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdSkuAttrVO>> findByPage(PrdSkuAttrQueryDTO queryDto) {
        return AjaxResult.success(prdskuattrService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdSkuAttrVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdskuattrService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加SKU规格关联（复合主键）")
    public AjaxResult<Void> add(@Valid @RequestBody PrdSkuAttrAddDTO addDto) {
        prdskuattrService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改SKU规格关联（复合主键）")
    public AjaxResult<Void> update(@Valid @RequestBody PrdSkuAttrUpdateDTO updateDto) {
        prdskuattrService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除SKU规格关联（复合主键）")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdskuattrService.delete(id);
        return AjaxResult.success();
    }

}
