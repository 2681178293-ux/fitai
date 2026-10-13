package net.wanho.fitai.modules.auth.model.dto;

import lombok.Data;

@Data
public class AdminProfileUpdateDTO {

    private String avatar;
    private String email;
    private String nickname;
    private String phonenumber;
    private String sex;
}
