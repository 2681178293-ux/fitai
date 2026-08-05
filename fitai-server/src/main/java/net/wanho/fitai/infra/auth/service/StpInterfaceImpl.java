package net.wanho.fitai.infra.auth.service;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }
}
