package net.wanho.fitai.modules.auth.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.auth.helper.StpKit;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.auth.model.dto.AdminLoginDTO;
import net.wanho.fitai.modules.auth.model.dto.AdminProfileUpdateDTO;
import net.wanho.fitai.modules.auth.service.AdminAuthServiceI;
import net.wanho.fitai.modules.auth.model.vo.AdminLoginVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员认证")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthServiceI adminAuthService;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public AjaxResult<AdminLoginVO> login(@RequestBody @Valid AdminLoginDTO loginDto) {
        return AjaxResult.success(adminAuthService.login(loginDto));
    }

    @GetMapping("/info")
    @Operation(summary = "获取管理员信息")
    public AjaxResult<AdminLoginVO> getInfo() {
        return AjaxResult.success(adminAuthService.getCurrentUserInfo());
    }

    @PostMapping("/logout")
    @Operation(summary = "管理员登出")
    public AjaxResult<Void> logout() {
        StpKit.ADMIN.logout();
        return AjaxResult.success();
    }

    @PutMapping("/profile")
    @Operation(summary = "修改管理员信息")
    public AjaxResult<Void> updateProfile(@RequestBody @Valid AdminProfileUpdateDTO updateDto) {
        adminAuthService.updateProfile(updateDto);
        return AjaxResult.success();
    }
}
