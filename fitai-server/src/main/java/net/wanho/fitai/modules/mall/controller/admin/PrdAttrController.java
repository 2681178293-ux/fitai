package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdAttrVO;
import net.wanho.fitai.modules.mall.service.PrdAttrServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdAttrController")
@RequestMapping("/admin/product/attr")
@RequiredArgsConstructor
@Tag(name = "商品属性接口")
public class PrdAttrController {

    private final PrdAttrServiceI prdattrService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdAttrVO>> findByPage(PrdAttrQueryDTO queryDto) {
        return AjaxResult.success(prdattrService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdAttrVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdattrService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加商品属性")
    public AjaxResult<Void> add(@Valid @RequestBody PrdAttrAddDTO addDto) {
        prdattrService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改商品属性")
    public AjaxResult<Void> update(@Valid @RequestBody PrdAttrUpdateDTO updateDto) {
        prdattrService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除商品属性")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdattrService.delete(id);
        return AjaxResult.success();
    }

}
