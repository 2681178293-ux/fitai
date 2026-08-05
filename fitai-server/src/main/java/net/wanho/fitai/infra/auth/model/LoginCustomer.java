package net.wanho.fitai.infra.auth.model;

import lombok.Data;

import java.io.Serializable;

/**
 * C 端登录缓存信息（存入 Sa-Token Session，避免重复查库）。
 */
@Data
public class LoginCustomer implements Serializable {

    private Long customerId;
    private String nickName;
    private String mobile;
    private String avatar;
}
