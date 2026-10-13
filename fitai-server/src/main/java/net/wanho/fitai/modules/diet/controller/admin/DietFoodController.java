package net.wanho.fitai.modules.diet.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.diet.model.dto.DietFoodAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietFoodQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietFoodUpdateDTO;
import net.wanho.fitai.modules.diet.model.vo.DietFoodVO;
import net.wanho.fitai.modules.diet.service.DietFoodServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminDietFoodController")
@RequestMapping("/admin/diet/food")
@RequiredArgsConstructor
@Tag(name = "食物库接口")
public class DietFoodController {

    private final DietFoodServiceI dietfoodService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<DietFoodVO>> findByPage(DietFoodQueryDTO queryDto) {
        return AjaxResult.success(dietfoodService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<DietFoodVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(dietfoodService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加食物库")
    public AjaxResult<Void> add(@Valid @RequestBody DietFoodAddDTO addDto) {
        dietfoodService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改食物库")
    public AjaxResult<Void> update(@Valid @RequestBody DietFoodUpdateDTO updateDto) {
        dietfoodService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除食物库")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        dietfoodService.delete(id);
        return AjaxResult.success();
    }

}
