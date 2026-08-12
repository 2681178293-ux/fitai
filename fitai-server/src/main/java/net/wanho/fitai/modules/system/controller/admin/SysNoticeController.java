package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysNoticeAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysNoticeRead;
import net.wanho.fitai.modules.system.model.vo.SysNoticeVO;
import net.wanho.fitai.modules.system.service.SysNoticeServiceI;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminSysNoticeController")
@RequestMapping("/admin/system/notice")
@RequiredArgsConstructor
@Tag(name = "通知公告接口")
public class SysNoticeController {

    private final SysNoticeServiceI sysNoticeService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:notice:list")
    public AjaxResult<IPage<SysNoticeVO>> findByPage(SysNoticeQueryDTO queryDto) {
        return AjaxResult.success(sysNoticeService.findByPage(queryDto));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:notice:query")
    public AjaxResult<SysNoticeVO> findById(@PathVariable("noticeId") Long noticeId) {
        return AjaxResult.success(sysNoticeService.findById(noticeId));
    }

    @GetMapping("/{noticeId}/read-stats")
    @Operation(summary = "查询阅读统计")
    @SaCheckPermission("system:notice:query")
    public AjaxResult<Long> readStats(@PathVariable("noticeId") Long noticeId) {
        return AjaxResult.success(  sysNoticeService.readStats(noticeId));
    }

    @GetMapping("/{noticeId}/read-list")
    @Operation(summary = "阅读详情")
    @SaCheckPermission("system:notice:query")
    public AjaxResult<List<SysNoticeRead>> readList(@PathVariable("noticeId") Long noticeId) {
        return AjaxResult.success( sysNoticeService.readList(noticeId));
    }

    @PostMapping("/{noticeId}/read")
    @Operation(summary = "标记已读")
    private AjaxResult<Void> markRead(@PathVariable("noticeId") Long noticeId) {
        sysNoticeService.markRead(noticeId);
        return AjaxResult.success();
    }

    @GetMapping("/my-read-ids")
    @Operation(summary = "我的已读")
    @SaCheckPermission("system:notice:query")
    public AjaxResult<List<Long>> myReadIds() {
        return AjaxResult.success(sysNoticeService.myReadIds());
    }

    @PostMapping("/read-batch")
    @Operation(summary = "批量阅读")
    @SaCheckPermission("system:notice:update")
    public AjaxResult<Void> readBatch(@RequestBody SysNoticeReadDTO readDTO) {
        sysNoticeService.readBatch(readDTO.getNoticeIds());
        return AjaxResult.success();
    }


    @PutMapping("/{noticeId}/publish")
    @Operation(summary = "发布")
    @SaCheckPermission("system:notice:update")
    public AjaxResult<Void> publish(@PathVariable("noticeId") Long noticeId) {
        sysNoticeService.publish(noticeId);
        return AjaxResult.success();
    }

    @PutMapping("/{noticeId}/revoke")
    @Operation(summary = "撤回")
    @SaCheckPermission("system:notice:update")
    public AjaxResult<Void> revoke(@PathVariable("noticeId") Long noticeId) {
        sysNoticeService.revoke(noticeId);
        return AjaxResult.success();
    }



    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:notice:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysNoticeAddDTO addDto) {
        sysNoticeService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:notice:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysNoticeUpdateDTO updateDto) {
        sysNoticeService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:notice:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysNoticeService.delete(ids);
        return AjaxResult.success();
    }
}
