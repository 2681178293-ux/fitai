package net.wanho.fitai.modules.auth.service;

import jakarta.validation.Valid;
import net.wanho.fitai.modules.auth.model.dto.AdminLoginDTO;
import net.wanho.fitai.modules.auth.model.dto.AdminProfileUpdateDTO;
import net.wanho.fitai.modules.auth.model.vo.AdminLoginVO;

public interface AdminAuthServiceI {

    AdminLoginVO login(AdminLoginDTO loginDto);

    AdminLoginVO getCurrentUserInfo();

    void updateProfile(@Valid AdminProfileUpdateDTO updateDto);
}
