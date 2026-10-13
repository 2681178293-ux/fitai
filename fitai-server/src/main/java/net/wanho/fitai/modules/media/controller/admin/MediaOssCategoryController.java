package net.wanho.fitai.modules.media.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.media.model.dto.MediaOssCategoryAddDTO;
import net.wanho.fitai.modules.media.model.dto.MediaOssCategoryQueryDTO;
import net.wanho.fitai.modules.media.model.dto.MediaOssCategoryUpdateDTO;
import net.wanho.fitai.modules.media.model.vo.MediaOssCategoryVO;
import net.wanho.fitai.modules.media.service.MediaOssCategoryServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMediaOssCategoryController")
@RequestMapping("/admin/media/oss-category")
@RequiredArgsConstructor
@Tag(name = "OSS图片分类接口")
public class MediaOssCategoryController {

    private final MediaOssCategoryServiceI mediaosscategoryService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MediaOssCategoryVO>> findByPage(MediaOssCategoryQueryDTO queryDto) {
        return AjaxResult.success(mediaosscategoryService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MediaOssCategoryVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mediaosscategoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加OSS图片分类")
    public AjaxResult<Void> add(@Valid @RequestBody MediaOssCategoryAddDTO addDto) {
        mediaosscategoryService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改OSS图片分类")
    public AjaxResult<Void> update(@Valid @RequestBody MediaOssCategoryUpdateDTO updateDto) {
        mediaosscategoryService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除OSS图片分类")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mediaosscategoryService.delete(id);
        return AjaxResult.success();
    }

}
