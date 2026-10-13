package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdCartAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCartQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdCartUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdCartVO;
import net.wanho.fitai.modules.mall.service.PrdCartServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdCartController")
@RequestMapping("/admin/product/cart")
@RequiredArgsConstructor
@Tag(name = "用户购物车(SKU)接口")
public class PrdCartController {

    private final PrdCartServiceI prdcartService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdCartVO>> findByPage(PrdCartQueryDTO queryDto) {
        return AjaxResult.success(prdcartService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdCartVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdcartService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户购物车(SKU)")
    public AjaxResult<Void> add(@Valid @RequestBody PrdCartAddDTO addDto) {
        prdcartService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户购物车(SKU)")
    public AjaxResult<Void> update(@Valid @RequestBody PrdCartUpdateDTO updateDto) {
        prdcartService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户购物车(SKU)")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdcartService.delete(id);
        return AjaxResult.success();
    }

}
