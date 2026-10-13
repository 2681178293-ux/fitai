package net.wanho.fitai.modules.media.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.media.model.dto.MediaVodCategoryAddDTO;
import net.wanho.fitai.modules.media.model.dto.MediaVodCategoryQueryDTO;
import net.wanho.fitai.modules.media.model.dto.MediaVodCategoryUpdateDTO;
import net.wanho.fitai.modules.media.model.vo.MediaVodCategoryVO;
import net.wanho.fitai.modules.media.service.MediaVodCategoryServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMediaVodCategoryController")
@RequestMapping("/admin/media/vod-category")
@RequiredArgsConstructor
@Tag(name = "VOD视频分类接口")
public class MediaVodCategoryController {

    private final MediaVodCategoryServiceI mediavodcategoryService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MediaVodCategoryVO>> findByPage(MediaVodCategoryQueryDTO queryDto) {
        return AjaxResult.success(mediavodcategoryService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MediaVodCategoryVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mediavodcategoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加VOD视频分类")
    public AjaxResult<Void> add(@Valid @RequestBody MediaVodCategoryAddDTO addDto) {
        mediavodcategoryService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改VOD视频分类")
    public AjaxResult<Void> update(@Valid @RequestBody MediaVodCategoryUpdateDTO updateDto) {
        mediavodcategoryService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除VOD视频分类")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mediavodcategoryService.delete(id);
        return AjaxResult.success();
    }

}
