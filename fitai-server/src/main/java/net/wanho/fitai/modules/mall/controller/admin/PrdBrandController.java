package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdBrandAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdBrandQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdBrandUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdBrandVO;
import net.wanho.fitai.modules.mall.service.PrdBrandServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdBrandController")
@RequestMapping("/admin/product/brand")
@RequiredArgsConstructor
@Tag(name = "商品品牌接口")
public class PrdBrandController {

    private final PrdBrandServiceI prdbrandService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdBrandVO>> findByPage(PrdBrandQueryDTO queryDto) {
        return AjaxResult.success(prdbrandService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdBrandVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdbrandService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加商品品牌")
    public AjaxResult<Void> add(@Valid @RequestBody PrdBrandAddDTO addDto) {
        prdbrandService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改商品品牌")
    public AjaxResult<Void> update(@Valid @RequestBody PrdBrandUpdateDTO updateDto) {
        prdbrandService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除商品品牌")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdbrandService.delete(id);
        return AjaxResult.success();
    }

}
