package net.wanho.fitai.modules.system.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysConfigAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysConfigQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysConfigUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysConfig;
import net.wanho.fitai.modules.system.service.SysConfigServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AppSysConfigController")
@RequestMapping("/sys/config")
@RequiredArgsConstructor
@Tag(name = "系统参数接口")
public class SysConfigController {

    private final SysConfigServiceI sysConfigService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult findByPage(SysConfigQueryDTO queryDto) {
        IPage<SysConfig> page = sysConfigService.findByPage(queryDto);
        return AjaxResult.success(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult findById(@PathVariable("id") Long id) {
        return AjaxResult.success(sysConfigService.getById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    public AjaxResult add(@Valid @RequestBody SysConfigAddDTO addDto) {
        sysConfigService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    public AjaxResult update(@Valid @RequestBody SysConfigUpdateDTO updateDto) {
        sysConfigService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public AjaxResult delete(@PathVariable("id") String id) {
        sysConfigService.delete(id);
        return AjaxResult.success();
    }

}
