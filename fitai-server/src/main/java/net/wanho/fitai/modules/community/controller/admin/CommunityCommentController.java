package net.wanho.fitai.modules.community.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.community.model.dto.CommunityCommentAddDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityCommentQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityCommentUpdateDTO;
import net.wanho.fitai.modules.community.model.vo.CommunityCommentVO;
import net.wanho.fitai.modules.community.service.CommunityCommentServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCommunityCommentController")
@RequestMapping("/admin/community/comment")
@RequiredArgsConstructor
@Tag(name = "社区评论表接口")
public class CommunityCommentController {

    private final CommunityCommentServiceI communitycommentService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CommunityCommentVO>> findByPage(CommunityCommentQueryDTO queryDto) {
        return AjaxResult.success(communitycommentService.findByPage(queryDto));
    }

    @PutMapping("/{id}/top")
    @Operation(summary = "置顶")
    public AjaxResult<Void> top(@PathVariable("id") Long id, @RequestParam("top") Integer top) {
        communitycommentService.top(id, top);
        return AjaxResult.success();
    }






    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CommunityCommentVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(communitycommentService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加社区评论表")
    public AjaxResult<Void> add(@Valid @RequestBody CommunityCommentAddDTO addDto) {
        communitycommentService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改社区评论表")
    public AjaxResult<Void> update(@Valid @RequestBody CommunityCommentUpdateDTO updateDto) {
        communitycommentService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除社区评论表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        communitycommentService.delete(id);
        return AjaxResult.success();
    }

}
