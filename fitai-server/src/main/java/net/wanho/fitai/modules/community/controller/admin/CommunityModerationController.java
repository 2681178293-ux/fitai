package net.wanho.fitai.modules.community.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.community.model.dto.*;
import net.wanho.fitai.modules.community.model.vo.CommunityModerationVO;
import net.wanho.fitai.modules.community.service.CommunityModerationServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCommunityModerationController")
@RequestMapping("/admin/community/moderation")
@RequiredArgsConstructor
@Tag(name = "社区内容审核表接口")
public class CommunityModerationController {

    private final CommunityModerationServiceI communitymoderationService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CommunityModerationVO>> findByPage(CommunityModerationQueryDTO queryDto) {
        return AjaxResult.success(communitymoderationService.findByPage(queryDto));

    }

    @PutMapping("/reject")
    @Operation(summary = "拒绝原因")
    public AjaxResult<Void> reject(@RequestBody CommunityModerationRejectDTO rejectDto) {
        communitymoderationService.reject(rejectDto);
        return AjaxResult.success();
    }

    @GetMapping("/history")
    @Operation(summary = "审核历史")
    public AjaxResult<IPage<CommunityModerationVO>> historyPage(CommunityModerationHistoryQueryDTO queryDto) {
        return AjaxResult.success(communitymoderationService.historyPage(queryDto));
    }







    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CommunityModerationVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(communitymoderationService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加社区内容审核表")
    public AjaxResult<Void> add(@Valid @RequestBody CommunityModerationAddDTO addDto) {
        communitymoderationService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改社区内容审核表")
    public AjaxResult<Void> update(@Valid @RequestBody CommunityModerationUpdateDTO updateDto) {
        communitymoderationService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除社区内容审核表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        communitymoderationService.delete(id);
        return AjaxResult.success();
    }

}
