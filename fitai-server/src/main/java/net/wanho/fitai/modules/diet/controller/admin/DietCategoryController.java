package net.wanho.fitai.modules.diet.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.diet.model.dto.DietCategoryAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietCategoryQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietCategoryUpdateDTO;
import net.wanho.fitai.modules.diet.model.vo.DietCategoryVO;
import net.wanho.fitai.modules.diet.service.DietCategoryServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminDietCategoryController")
@RequestMapping("/admin/diet/category")
@RequiredArgsConstructor
@Tag(name = "饮食分类接口")
public class DietCategoryController {

    private final DietCategoryServiceI dietcategoryService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<DietCategoryVO>> findByPage(DietCategoryQueryDTO queryDto) {
        return AjaxResult.success(dietcategoryService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<DietCategoryVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(dietcategoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加饮食分类")
    public AjaxResult<Void> add(@Valid @RequestBody DietCategoryAddDTO addDto) {
        dietcategoryService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改饮食分类")
    public AjaxResult<Void> update(@Valid @RequestBody DietCategoryUpdateDTO updateDto) {
        dietcategoryService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除饮食分类")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        dietcategoryService.delete(id);
        return AjaxResult.success();
    }

}
