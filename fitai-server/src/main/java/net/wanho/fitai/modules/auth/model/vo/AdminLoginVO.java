package net.wanho.fitai.modules.auth.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminLoginVO {

    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phonenumber;
    private List<String> roles;
    private List<String> permissions;
}
