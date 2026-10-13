package net.wanho.fitai.modules.media.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.vo.MediaVodCategoryVO;
import net.wanho.fitai.modules.media.model.vo.MediaVodVO;
import net.wanho.fitai.modules.media.service.MediaVodCategoryServiceI;
import net.wanho.fitai.modules.media.service.MediaVodServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminMediaVodController")
@RequestMapping("/admin/media/vod")
@RequiredArgsConstructor
@Tag(name = "VOD视频接口")
public class MediaVodController {

    private final MediaVodServiceI mediavodService;
    private final MediaVodCategoryServiceI mediaVodCategoryService;


    @PutMapping("/category/move")
    @Operation(summary = "移动分类")
    public AjaxResult<Void> moveCategory(@Valid @RequestBody MediaVodCategoryMoveDTO moveDto) {
        mediaVodCategoryService.moveCategory(moveDto);
        return AjaxResult.success();
    }

    @GetMapping("/{videoId}/play-url")
    @Operation(summary = "获取播放地址")
    public AjaxResult<String> getPlayUrl(@PathVariable("videoId") Long id) {
        return AjaxResult.success(mediavodService.getPlayUrl(id));
    }


    @GetMapping("/page")
    public AjaxResult<IPage<MediaVodVO>> findByPage(MediaVodQuerySimpleDTO queryDto) {
        return AjaxResult.success(mediavodService.findByPage(queryDto));
    }

    @GetMapping("/category/tree")
    @Operation(summary = "查询分类树")
    public AjaxResult<List<MediaVodCategoryVO>> getCategoryTree() {
        return AjaxResult.success(mediaVodCategoryService.getCategoryTree()) ;
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MediaVodVO>> findByPage(MediaVodQueryDTO queryDto) {
        return AjaxResult.success(mediavodService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MediaVodVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mediavodService.findById(id));
    }

    @PostMapping("/upload")
    public AjaxResult<MediaVodVO> upload(MediaVodUploadDTO uploadDto) {
        return AjaxResult.success(mediavodService.upload(uploadDto));
    }

    @PostMapping("/category")
    public AjaxResult<Void> addCategory(@Valid @RequestBody MediaVodCategorySimpleAddDTO addDto) {
        mediaVodCategoryService.addCategory(addDto);
        return AjaxResult.success();
    }
    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加VOD视频")
    public AjaxResult<Void> add(@Valid @RequestBody MediaVodAddDTO addDto) {
        mediavodService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping("/category")
    @Operation(summary = "修改")
    @OperLog(title = "修改VOD视频")
    public AjaxResult<Void> update(@Valid @RequestBody MediaVodCategoryUpdateDTO updateDto) {
        mediaVodCategoryService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/category/{categoryId}")
    @Operation(summary = "删除分类")
    @OperLog(title = "删除OSS图片分类")
    public AjaxResult<Void> deleteCategory(@PathVariable("categoryId") String categoryId) {
        mediaVodCategoryService.deleteCategory(categoryId);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除VOD视频")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mediavodService.delete(id);
        return AjaxResult.success();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @OperLog(title = "删除vod视频")
    public AjaxResult<Void> delete(@RequestBody MediaVodDeleteDTO deleteDTO) {
        mediavodService.delete(deleteDTO);
        return AjaxResult.success();
    }

}
