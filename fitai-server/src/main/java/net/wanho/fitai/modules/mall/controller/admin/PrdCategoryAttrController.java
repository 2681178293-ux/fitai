package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAttrAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAttrQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAttrUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdCategoryAttrVO;
import net.wanho.fitai.modules.mall.service.PrdCategoryAttrServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdCategoryAttrController")
@RequestMapping("/admin/product/category-attr")
@RequiredArgsConstructor
@Tag(name = "分类关联属性（复合主键）接口")
public class PrdCategoryAttrController {

    private final PrdCategoryAttrServiceI prdcategoryattrService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdCategoryAttrVO>> findByPage(PrdCategoryAttrQueryDTO queryDto) {
        return AjaxResult.success(prdcategoryattrService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdCategoryAttrVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdcategoryattrService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加分类关联属性（复合主键）")
    public AjaxResult<Void> add(@Valid @RequestBody PrdCategoryAttrAddDTO addDto) {
        prdcategoryattrService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改分类关联属性（复合主键）")
    public AjaxResult<Void> update(@Valid @RequestBody PrdCategoryAttrUpdateDTO updateDto) {
        prdcategoryattrService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除分类关联属性（复合主键）")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdcategoryattrService.delete(id);
        return AjaxResult.success();
    }

}
