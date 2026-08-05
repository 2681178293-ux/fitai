package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysUserAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysUserQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysUserUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.SysUserVO;
import net.wanho.fitai.modules.system.service.SysUserServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysUserController")
@RequestMapping("/admin/system/user")
@RequiredArgsConstructor
@Tag(name = "后台用户接口")
public class SysUserController {

    private final SysUserServiceI sysUserService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:user:list")
    public AjaxResult<IPage<SysUserVO>> findByPage(SysUserQueryDTO queryDto) {
        return AjaxResult.success(sysUserService.findByPage(queryDto));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:user:query")
    public AjaxResult<SysUserVO> findById(@PathVariable("userId") Long userId) {
        return AjaxResult.success(sysUserService.findById(userId));
    }

    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:user:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysUserAddDTO addDto) {
        sysUserService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:user:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysUserUpdateDTO updateDto) {
        sysUserService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:user:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysUserService.delete(ids);
        return AjaxResult.success();
    }
}
