package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysMenuAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysMenuQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysMenuUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.RouterVO;
import net.wanho.fitai.modules.system.model.vo.SysMenuVO;
import net.wanho.fitai.modules.system.service.SysMenuServiceI;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminSysMenuController")
@RequestMapping("/admin/system/menu")
@RequiredArgsConstructor
@Tag(name = "菜单管理接口")
public class SysMenuController {

    private final SysMenuServiceI sysMenuService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:menu:list")
    public AjaxResult<IPage<SysMenuVO>> findByPage(SysMenuQueryDTO queryDto) {
        return AjaxResult.success(sysMenuService.findByPage(queryDto));
    }

    @GetMapping("/routers")
    @Operation(summary = "当前用户前端路由")
    public AjaxResult<List<RouterVO>> routers() {
        return AjaxResult.success(sysMenuService.buildRoutersForCurrentUser());
    }

    @GetMapping("/{menuId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:menu:query")
    public AjaxResult<SysMenuVO> findById(@PathVariable("menuId") Long menuId) {
        return AjaxResult.success(sysMenuService.findById(menuId));
    }

    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:menu:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysMenuAddDTO addDto) {
        sysMenuService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:menu:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysMenuUpdateDTO updateDto) {
        sysMenuService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:menu:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysMenuService.delete(ids);
        return AjaxResult.success();
    }
}
