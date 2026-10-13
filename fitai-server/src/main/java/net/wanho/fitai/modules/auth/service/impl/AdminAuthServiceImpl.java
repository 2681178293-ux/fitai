package net.wanho.fitai.modules.auth.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.auth.constant.CommonConstant;
import net.wanho.fitai.infra.auth.helper.AdminLoginHelper;
import net.wanho.fitai.infra.auth.helper.StpKit;
import net.wanho.fitai.infra.auth.model.LoginUser;
import net.wanho.fitai.infra.exception.CustomException;
import net.wanho.fitai.modules.auth.model.dto.AdminLoginDTO;
import net.wanho.fitai.modules.auth.model.dto.AdminProfileUpdateDTO;
import net.wanho.fitai.modules.auth.service.AdminAuthServiceI;
import net.wanho.fitai.modules.auth.model.vo.AdminLoginVO;
import net.wanho.fitai.modules.system.code.SystemCode;
import net.wanho.fitai.modules.system.model.po.SysDept;
import net.wanho.fitai.modules.system.model.po.SysUser;
import net.wanho.fitai.modules.system.service.SysDeptServiceI;
import net.wanho.fitai.modules.system.service.SysLoginLogServiceI;
import net.wanho.fitai.modules.system.service.SysUserServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthServiceI {

    private final SysUserServiceI sysUserService;
    private final SysDeptServiceI sysDeptService;
    private final SysLoginLogServiceI loginLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminLoginVO login(AdminLoginDTO loginDto) {
        SysUser user = sysUserService.getByUserName(loginDto.getUsername());

        if (ObjectUtil.isNull(user)) {
            loginLogService.recordLoginLog(loginDto.getUsername(), "admin","admin","0", SystemCode.USER_NOT_FOUND.msg());
            throw new CustomException(SystemCode.USER_NOT_FOUND);
        }
        if (!CommonConstant.STATUS_NORMAL.equals(user.getStatus())) {
            loginLogService.recordLoginLog(loginDto.getUsername(), "admin","admin","0", SystemCode.USER_DISABLED.msg());
            throw new CustomException(SystemCode.USER_DISABLED);
        }
        if (!BCrypt.checkpw(loginDto.getPassword(), user.getPassword())) {
            loginLogService.recordLoginLog(loginDto.getUsername(), "admin","admin","0", SystemCode.PASSWORD_ERROR.msg());
            throw new CustomException(SystemCode.PASSWORD_ERROR);
        }

        StpKit.ADMIN.login(user.getUserId());
        LoginUser loginUser = buildLoginUser(user);
        StpKit.ADMIN.getSession().set(SaSession.USER, loginUser);
        updateLoginInfo(user);
        loginLogService.recordLoginLog(user.getUserName(), "admin","admin","1", "登录成功");
        return buildUserInfo(user, StpKit.ADMIN.getTokenValue());
    }

    //获取当前用户信息
    @Override
    public AdminLoginVO getCurrentUserInfo() {
        Long userId = AdminLoginHelper.getUserId();
        if (ObjectUtil.isNull(userId)) {
            throw new CustomException(SystemCode.USER_NOT_FOUND);
        }
        SysUser sysUser = sysUserService.getById(userId);
        if (ObjectUtil.isNull(sysUser)) {
            throw new CustomException(SystemCode.USER_NOT_FOUND);
        }
        // Session 无缓存或缺少权限字段时补写（兼容旧 token）
        LoginUser cached = AdminLoginHelper.getLoginUser();
        if (ObjectUtil.isNull(cached) || ObjectUtil.isNull(cached.getPermissions())) {
            LoginUser loginUser = buildLoginUser(sysUser);
            StpKit.ADMIN.getSession().set(SaSession.USER, loginUser);
        }
        return buildUserInfo(sysUser, StpKit.ADMIN.getTokenValue());
    }

    @Override
    public void updateProfile(AdminProfileUpdateDTO updateDto) {

        Long userId = AdminLoginHelper.getUserId();
        SysUser sysUser = sysUserService.getById(userId);
        String nickName = sysUser.getNickName();
        sysUser.setNickName(updateDto.getNickname());
        sysUser.setAvatar(updateDto.getAvatar());
        sysUser.setEmail(updateDto.getEmail());
        sysUser.setPhonenumber(updateDto.getPhonenumber());
        sysUser.setSex(updateDto.getSex());
        sysUserService.updateById(sysUser);

        if(!nickName.equals(updateDto.getNickname())) {
            LoginUser loginUser = buildLoginUser(sysUser);
            StpKit.ADMIN.getSession().set(SaSession.USER, loginUser);
        }
    }

    private LoginUser buildLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUserName(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setDeptId(user.getDeptId());
        if (ObjectUtil.isNotNull(user.getDeptId())) {
            SysDept dept = sysDeptService.getById(user.getDeptId());
            if (ObjectUtil.isNotNull(dept)) {
                loginUser.setDeptName(dept.getDeptName());
            }
        }
        loginUser.setRoles(resolveRoles(user.getUserId()));
        loginUser.setPermissions(resolvePermissions(user.getUserId()));
        return loginUser;
    }

    private List<String> resolveRoles(Long userId) {
        if (CommonConstant.SUPER_ADMIN_ID.equals(userId)) {
            return sysUserService.getAllRoleKeys();
        }
        return sysUserService.getRoleKeysByUserId(userId);
    }

    private List<String> resolvePermissions(Long userId) {
        if (CommonConstant.SUPER_ADMIN_ID.equals(userId)) {
            return sysUserService.getAllPerms();
        }
        return sysUserService.getPermsByUserId(userId);
    }

    private void updateLoginInfo(SysUser user) {
        SysUser updateUser = new SysUser();
        updateUser.setUserId(user.getUserId());
        updateUser.setLoginDate(LocalDateTime.now());
        sysUserService.updateById(updateUser);
    }

    private AdminLoginVO buildUserInfo(SysUser user, String token) {
        LoginUser loginUser = AdminLoginHelper.getLoginUser();
        List<String> roles = ObjectUtil.isNotNull(loginUser) ? loginUser.getRoles() : resolveRoles(user.getUserId());
        List<String> permissions = ObjectUtil.isNotNull(loginUser) ? loginUser.getPermissions() : resolvePermissions(user.getUserId());

        AdminLoginVO vo = new AdminLoginVO();
        vo.setToken(token);
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUserName());
        vo.setNickname(user.getNickName());
        vo.setAvatar(user.getAvatar());
        vo.setRoles(roles);
        vo.setEmail(user.getEmail());
        vo.setPhonenumber(user.getPhonenumber());
        vo.setPermissions(permissions);
        return vo;
    }
}
