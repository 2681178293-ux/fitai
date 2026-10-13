package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCategoryUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdCategoryVO;
import net.wanho.fitai.modules.mall.service.PrdCategoryServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminPrdCategoryController")
@RequestMapping("/admin/product/category")
@RequiredArgsConstructor
@Tag(name = "商品分类(树)接口")
public class PrdCategoryController {

    private final PrdCategoryServiceI prdcategoryService;

    @GetMapping("/tree")
    @Operation(summary = "分页查询")
    public AjaxResult<List<PrdCategoryVO>> tree(PrdCategoryQueryDTO queryDto) {
        return AjaxResult.success(prdcategoryService.tree(queryDto));
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdCategoryVO>> findByPage(PrdCategoryQueryDTO queryDto) {
        return AjaxResult.success(prdcategoryService.findByPage(queryDto));
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "查询")
    public AjaxResult<PrdCategoryVO> findById(@PathVariable("categoryId") Long categoryId) {
        return AjaxResult.success(prdcategoryService.findById(categoryId));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加商品分类(树)")
    public AjaxResult<Void> add(@Valid @RequestBody PrdCategoryAddDTO addDto) {
        prdcategoryService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping("/{categoryId}/status")
    @Operation(summary = "修改商品分类状态")
    public AjaxResult<Void> updateStatus(@PathVariable("categoryId") Long categoryId, @RequestParam String status) {
        prdcategoryService.updateStatus(categoryId, status);
        return AjaxResult.success();
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "修改商品分类状态")
    @OperLog(title = "修改商品分类状态")
    public AjaxResult<Void> update(@PathVariable("categoryId") Long categoryId,  @RequestBody PrdCategoryUpdateDTO updateDto) {
        prdcategoryService.update(updateDto);
        return AjaxResult.success();
    }



    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改商品分类(树)")
    public AjaxResult<Void> update(@Valid @RequestBody PrdCategoryUpdateDTO updateDto) {
        prdcategoryService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除商品分类(树)")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdcategoryService.delete(id);
        return AjaxResult.success();
    }

}
