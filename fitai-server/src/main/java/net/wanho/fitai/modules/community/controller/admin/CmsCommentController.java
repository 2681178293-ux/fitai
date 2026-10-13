package net.wanho.fitai.modules.community.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.community.model.dto.CmsCommentAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentUpdateDTO;
import net.wanho.fitai.modules.community.model.vo.CmsCommentVO;
import net.wanho.fitai.modules.community.service.CmsCommentServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCmsCommentController")
@RequestMapping("/admin/community/cms-comment")
@RequiredArgsConstructor
@Tag(name = "社区评论接口")
public class CmsCommentController {

    private final CmsCommentServiceI cmscommentService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CmsCommentVO>> findByPage(CmsCommentQueryDTO queryDto) {
        return AjaxResult.success(cmscommentService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CmsCommentVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(cmscommentService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加社区评论")
    public AjaxResult<Void> add(@Valid @RequestBody CmsCommentAddDTO addDto) {
        cmscommentService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改社区评论")
    public AjaxResult<Void> update(@Valid @RequestBody CmsCommentUpdateDTO updateDto) {
        cmscommentService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除社区评论")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        cmscommentService.delete(id);
        return AjaxResult.success();
    }

}
