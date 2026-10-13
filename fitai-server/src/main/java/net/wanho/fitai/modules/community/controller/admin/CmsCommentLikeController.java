package net.wanho.fitai.modules.community.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.community.model.dto.CmsCommentLikeAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentLikeQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentLikeUpdateDTO;
import net.wanho.fitai.modules.community.model.vo.CmsCommentLikeVO;
import net.wanho.fitai.modules.community.service.CmsCommentLikeServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCmsCommentLikeController")
@RequestMapping("/admin/community/comment-like")
@RequiredArgsConstructor
@Tag(name = "评论点赞接口")
public class CmsCommentLikeController {

    private final CmsCommentLikeServiceI cmscommentlikeService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CmsCommentLikeVO>> findByPage(CmsCommentLikeQueryDTO queryDto) {
        return AjaxResult.success(cmscommentlikeService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CmsCommentLikeVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(cmscommentlikeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加评论点赞")
    public AjaxResult<Void> add(@Valid @RequestBody CmsCommentLikeAddDTO addDto) {
        cmscommentlikeService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改评论点赞")
    public AjaxResult<Void> update(@Valid @RequestBody CmsCommentLikeUpdateDTO updateDto) {
        cmscommentlikeService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除评论点赞")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        cmscommentlikeService.delete(id);
        return AjaxResult.success();
    }

}
