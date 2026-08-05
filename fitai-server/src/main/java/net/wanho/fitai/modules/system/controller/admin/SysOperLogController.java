package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysOperLogQueryDTO;
import net.wanho.fitai.modules.system.model.vo.SysOperLogVO;
import net.wanho.fitai.modules.system.service.SysOperLogServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysOperLogController")
@RequestMapping("/admin/system/oper-log")
@RequiredArgsConstructor
@Tag(name = "操作日志接口")
public class SysOperLogController {

    private final SysOperLogServiceI sysOperLogService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:oper-log:list")
    public AjaxResult<IPage<SysOperLogVO>> findByPage(SysOperLogQueryDTO queryDto) {
        return AjaxResult.success(sysOperLogService.findByPage(queryDto));
    }

    @GetMapping("/{operId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:oper-log:query")
    public AjaxResult<SysOperLogVO> findById(@PathVariable("operId") Long operId) {
        return AjaxResult.success(sysOperLogService.findById(operId));
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:oper-log:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysOperLogService.delete(ids);
        return AjaxResult.success();
    }
}
