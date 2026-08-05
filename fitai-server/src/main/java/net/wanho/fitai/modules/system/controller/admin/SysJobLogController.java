package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysJobLogQueryDTO;
import net.wanho.fitai.modules.system.model.vo.SysJobLogVO;
import net.wanho.fitai.modules.system.service.SysJobLogServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysJobLogController")
@RequestMapping("/admin/system/job/log")
@RequiredArgsConstructor
@Tag(name = "定时任务日志接口")
public class SysJobLogController {

    private final SysJobLogServiceI sysJobLogService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:job:log:list")
    public AjaxResult<IPage<SysJobLogVO>> findByPage(SysJobLogQueryDTO queryDto) {
        return AjaxResult.success(sysJobLogService.findByPage(queryDto));
    }

    @GetMapping("/{jobLogId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:job:log:query")
    public AjaxResult<SysJobLogVO> findById(@PathVariable("jobLogId") Long jobLogId) {
        return AjaxResult.success(sysJobLogService.findById(jobLogId));
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:job:log:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysJobLogService.delete(ids);
        return AjaxResult.success();
    }
}
