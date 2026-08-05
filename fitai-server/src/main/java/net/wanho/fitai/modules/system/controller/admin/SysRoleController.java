package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysRoleAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysRoleQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysRoleUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.SysRoleVO;
import net.wanho.fitai.modules.system.service.SysRoleServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysRoleController")
@RequestMapping("/admin/system/role")
@RequiredArgsConstructor
@Tag(name = "角色管理接口")
public class SysRoleController {

    private final SysRoleServiceI sysRoleService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:role:list")
    public AjaxResult<IPage<SysRoleVO>> findByPage(SysRoleQueryDTO queryDto) {
        return AjaxResult.success(sysRoleService.findByPage(queryDto));
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:role:query")
    public AjaxResult<SysRoleVO> findById(@PathVariable("roleId") Long roleId) {
        return AjaxResult.success(sysRoleService.findById(roleId));
    }

    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:role:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysRoleAddDTO addDto) {
        sysRoleService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:role:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysRoleUpdateDTO updateDto) {
        sysRoleService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:role:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysRoleService.delete(ids);
        return AjaxResult.success();
    }
}
