package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysJobAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysJobQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysJobUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.SysJobVO;
import net.wanho.fitai.modules.system.service.SysJobServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysJobController")
@RequestMapping("/admin/system/job")
@RequiredArgsConstructor
@Tag(name = "定时任务接口")
public class SysJobController {

    private final SysJobServiceI sysJobService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:job:list")
    public AjaxResult<IPage<SysJobVO>> findByPage(SysJobQueryDTO queryDto) {
        return AjaxResult.success(sysJobService.findByPage(queryDto));
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:job:query")
    public AjaxResult<SysJobVO> findById(@PathVariable("jobId") Long jobId) {
        return AjaxResult.success(sysJobService.findById(jobId));
    }

    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:job:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysJobAddDTO addDto) {
        sysJobService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:job:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysJobUpdateDTO updateDto) {
        sysJobService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:job:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysJobService.delete(ids);
        return AjaxResult.success();
    }
}
