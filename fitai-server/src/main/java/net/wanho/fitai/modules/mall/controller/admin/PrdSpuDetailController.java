package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuDetailAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuDetailQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuDetailUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdSpuDetailVO;
import net.wanho.fitai.modules.mall.service.PrdSpuDetailServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdSpuDetailController")
@RequestMapping("/admin/product/spu-detail")
@RequiredArgsConstructor
@Tag(name = "SPU详情接口")
public class PrdSpuDetailController {

    private final PrdSpuDetailServiceI prdspudetailService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdSpuDetailVO>> findByPage(PrdSpuDetailQueryDTO queryDto) {
        return AjaxResult.success(prdspudetailService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdSpuDetailVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdspudetailService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加SPU详情")
    public AjaxResult<Void> add(@Valid @RequestBody PrdSpuDetailAddDTO addDto) {
        prdspudetailService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改SPU详情")
    public AjaxResult<Void> update(@Valid @RequestBody PrdSpuDetailUpdateDTO updateDto) {
        prdspudetailService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除SPU详情")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdspudetailService.delete(id);
        return AjaxResult.success();
    }

}
