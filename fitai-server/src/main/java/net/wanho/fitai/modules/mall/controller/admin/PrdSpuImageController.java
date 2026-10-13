package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuImageAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuImageQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuImageUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdSpuImageVO;
import net.wanho.fitai.modules.mall.service.PrdSpuImageServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdSpuImageController")
@RequestMapping("/admin/product/spu-image")
@RequiredArgsConstructor
@Tag(name = "SPU图片接口")
public class PrdSpuImageController {

    private final PrdSpuImageServiceI prdspuimageService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdSpuImageVO>> findByPage(PrdSpuImageQueryDTO queryDto) {
        return AjaxResult.success(prdspuimageService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdSpuImageVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prdspuimageService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加SPU图片")
    public AjaxResult<Void> add(@Valid @RequestBody PrdSpuImageAddDTO addDto) {
        prdspuimageService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改SPU图片")
    public AjaxResult<Void> update(@Valid @RequestBody PrdSpuImageUpdateDTO updateDto) {
        prdspuimageService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除SPU图片")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdspuimageService.delete(id);
        return AjaxResult.success();
    }

}
