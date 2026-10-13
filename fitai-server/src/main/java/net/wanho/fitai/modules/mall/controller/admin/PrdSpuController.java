package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdSpuVO;
import net.wanho.fitai.modules.mall.service.PrdSpuServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdSpuController")
@RequestMapping("/admin/product/spu")
@RequiredArgsConstructor
@Tag(name = "商品SPU接口")
public class PrdSpuController {

    private final PrdSpuServiceI prdspuService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdSpuVO>> findByPage(PrdSpuQueryDTO queryDto) {
        return AjaxResult.success(prdspuService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdSpuVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdspuService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加商品SPU")
    public AjaxResult<Void> add(@Valid @RequestBody PrdSpuAddDTO addDto) {
        prdspuService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改商品SPU")
    public AjaxResult<Void> update(@Valid @RequestBody PrdSpuUpdateDTO updateDto) {
        prdspuService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除商品SPU")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdspuService.delete(id);
        return AjaxResult.success();
    }

}
