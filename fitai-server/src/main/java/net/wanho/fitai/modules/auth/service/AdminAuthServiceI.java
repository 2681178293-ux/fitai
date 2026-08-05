package net.wanho.fitai.modules.auth.service;

import net.wanho.fitai.modules.auth.dto.AdminLoginDTO;
import net.wanho.fitai.modules.auth.vo.AdminLoginVO;

public interface AdminAuthServiceI {

    AdminLoginVO login(AdminLoginDTO loginDto);

    AdminLoginVO getCurrentUserInfo();
}
