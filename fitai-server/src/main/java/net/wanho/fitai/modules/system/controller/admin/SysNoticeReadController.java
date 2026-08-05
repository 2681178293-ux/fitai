package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeReadUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.SysNoticeReadVO;
import net.wanho.fitai.modules.system.service.SysNoticeReadServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysNoticeReadController")
@RequestMapping("/admin/system/notice/read")
@RequiredArgsConstructor
@Tag(name = "公告已读接口")
public class SysNoticeReadController {

    private final SysNoticeReadServiceI sysNoticeReadService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:notice:read:list")
    public AjaxResult<IPage<SysNoticeReadVO>> findByPage(SysNoticeReadQueryDTO queryDto) {
        return AjaxResult.success(sysNoticeReadService.findByPage(queryDto));
    }

    @GetMapping("/{readId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:notice:read:query")
    public AjaxResult<SysNoticeReadVO> findById(@PathVariable("readId") Long readId) {
        return AjaxResult.success(sysNoticeReadService.findById(readId));
    }

    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:notice:read:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysNoticeReadAddDTO addDto) {
        sysNoticeReadService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:notice:read:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysNoticeReadUpdateDTO updateDto) {
        sysNoticeReadService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:notice:read:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysNoticeReadService.delete(ids);
        return AjaxResult.success();
    }
}
