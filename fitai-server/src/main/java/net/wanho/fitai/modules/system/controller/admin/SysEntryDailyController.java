package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysEntryDailyAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysEntryDailyQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysEntryDailyUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.SysEntryDailyVO;
import net.wanho.fitai.modules.system.service.SysEntryDailyServiceI;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController("AdminSysEntryDailyController")
@RequestMapping("/admin/system/entry-daily")
@RequiredArgsConstructor
@Tag(name = "入口日统计接口")
public class SysEntryDailyController {

    private final SysEntryDailyServiceI sysEntryDailyService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:entry-daily:list")
    public AjaxResult<IPage<SysEntryDailyVO>> findByPage(SysEntryDailyQueryDTO queryDto) {
        return AjaxResult.success(sysEntryDailyService.findByPage(queryDto));
    }

    @GetMapping("/detail")
    @Operation(summary = "查询")
    @SaCheckPermission("system:entry-daily:query")
    public AjaxResult<SysEntryDailyVO> findById(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate statDate,
            @RequestParam String channel) {
        return AjaxResult.success(sysEntryDailyService.findById(statDate, channel));
    }

    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:entry-daily:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysEntryDailyAddDTO addDto) {
        sysEntryDailyService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:entry-daily:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysEntryDailyUpdateDTO updateDto) {
        sysEntryDailyService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @SaCheckPermission("system:entry-daily:delete")
    public AjaxResult<Void> delete(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate statDate,
            @RequestParam String channel) {
        sysEntryDailyService.delete(statDate, channel);
        return AjaxResult.success();
    }
}
