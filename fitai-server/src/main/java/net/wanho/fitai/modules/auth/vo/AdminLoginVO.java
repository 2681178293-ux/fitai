package net.wanho.fitai.modules.auth.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminLoginVO {

    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
    private String email;
    private String phoneNumber;
}
