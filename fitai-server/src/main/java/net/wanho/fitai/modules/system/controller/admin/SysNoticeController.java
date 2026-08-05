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
import net.wanho.fitai.modules.system.model.dto.SysNoticeUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.SysNoticeVO;
import net.wanho.fitai.modules.system.service.SysNoticeServiceI;
import org.springframework.web.bind.annotation.*;

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
