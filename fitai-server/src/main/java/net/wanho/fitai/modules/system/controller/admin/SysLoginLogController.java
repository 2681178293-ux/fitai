package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysLoginLogQueryDTO;
import net.wanho.fitai.modules.system.model.vo.SysLoginLogVO;
import net.wanho.fitai.modules.system.service.SysLoginLogServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysLoginLogController")
@RequestMapping("/admin/system/login-log")
@RequiredArgsConstructor
@Tag(name = "登录日志接口")
public class SysLoginLogController {

    private final SysLoginLogServiceI sysLoginLogService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:login-log:list")
    public AjaxResult<IPage<SysLoginLogVO>> findByPage(SysLoginLogQueryDTO queryDto) {
        return AjaxResult.success(sysLoginLogService.findByPage(queryDto));
    }

    @GetMapping("/{logId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:login-log:query")
    public AjaxResult<SysLoginLogVO> findById(@PathVariable("logId") Long logId) {
        return AjaxResult.success(sysLoginLogService.findById(logId));
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:login-log:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysLoginLogService.delete(ids);
        return AjaxResult.success();
    }
}
