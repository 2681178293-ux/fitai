package net.wanho.fitai.modules.media.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.vo.MediaOssCategoryVO;
import net.wanho.fitai.modules.media.model.vo.MediaOssVO;
import net.wanho.fitai.modules.media.service.MediaOssCategoryServiceI;
import net.wanho.fitai.modules.media.service.MediaOssServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminMediaOssController")
@RequestMapping("/admin/media/oss")
@RequiredArgsConstructor
@Tag(name = "OSS图片接口")
public class MediaOssController {

    private final MediaOssServiceI mediaossService;
    private final MediaOssCategoryServiceI mediaOssCategoryService;

    @GetMapping("/page")
    public AjaxResult<IPage<MediaOssVO>> findByPage(MediaOssQuerySimpleDTO queryDto) {
        return AjaxResult.success(mediaossService.findByPage(queryDto));
    }


    @PostMapping("/upload")
    public AjaxResult<MediaOssVO> upload(MediaOssUploadDTO uploadDto) {
        return AjaxResult.success(mediaossService.upload(uploadDto));
    }


    @DeleteMapping("/category/{categoryId}")
    @Operation(summary = "删除分类")
    @OperLog(title = "删除OSS图片分类")
    public AjaxResult<Void> deleteCategory(@PathVariable("categoryId") String categoryId) {
        mediaOssCategoryService.deleteCategory(categoryId);
        return AjaxResult.success();
    }

    @PostMapping("/category")
    @Operation(summary = "添加分类")
    @OperLog(title = "添加OSS图片分类")
    public AjaxResult<Void> addCategory(@Valid @RequestBody MediaOssCategorySimpleAddDTO addDto) {
        mediaOssCategoryService.addCategory(addDto);
        return AjaxResult.success();
    }

    @PutMapping("/category")
    public AjaxResult<Void> updateCategoryName(@RequestBody MediaOssCategoryUpdateNameDTO moveDto) {
        mediaOssCategoryService.updateCategoryName(moveDto);
        return AjaxResult.success();
    }


    @PutMapping("/category/move")
    public AjaxResult<Void> moveCategory(@RequestBody MediaOssCategoryMoveDTO moveDto) {
        mediaOssCategoryService.move(moveDto);
        return AjaxResult.success();
    }

    @GetMapping("/category/tree")
    public AjaxResult<List<MediaOssCategoryVO>> tree(){
       return AjaxResult.success(mediaOssCategoryService.tree());
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MediaOssVO>> findByPage(MediaOssQueryDTO queryDto) {
        return AjaxResult.success(mediaossService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MediaOssVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mediaossService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加OSS图片")
    public AjaxResult<Void> add(@Valid @RequestBody MediaOssAddDTO addDto) {
        mediaossService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改OSS图片")
    public AjaxResult<Void> update(@Valid @RequestBody MediaOssUpdateDTO updateDto) {
        mediaossService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @OperLog(title = "删除OSS图片")
    public AjaxResult<Void> delete(@RequestBody MediaOssDeleteDTO deleteDTO) {
        mediaossService.delete(deleteDTO);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除OSS图片")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mediaossService.delete(id);
        return AjaxResult.success();
    }

}
