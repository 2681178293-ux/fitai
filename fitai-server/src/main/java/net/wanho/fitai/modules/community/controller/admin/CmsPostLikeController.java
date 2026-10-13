package net.wanho.fitai.modules.community.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.community.model.dto.CmsPostLikeAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostLikeQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostLikeUpdateDTO;
import net.wanho.fitai.modules.community.model.vo.CmsPostLikeVO;
import net.wanho.fitai.modules.community.service.CmsPostLikeServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCmsPostLikeController")
@RequestMapping("/admin/community/post-like")
@RequiredArgsConstructor
@Tag(name = "动态点赞接口")
public class CmsPostLikeController {

    private final CmsPostLikeServiceI cmspostlikeService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CmsPostLikeVO>> findByPage(CmsPostLikeQueryDTO queryDto) {
        return AjaxResult.success(cmspostlikeService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CmsPostLikeVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(cmspostlikeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加动态点赞")
    public AjaxResult<Void> add(@Valid @RequestBody CmsPostLikeAddDTO addDto) {
        cmspostlikeService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改动态点赞")
    public AjaxResult<Void> update(@Valid @RequestBody CmsPostLikeUpdateDTO updateDto) {
        cmspostlikeService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除动态点赞")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        cmspostlikeService.delete(id);
        return AjaxResult.success();
    }

}
