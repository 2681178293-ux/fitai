package net.wanho.fitai.infra.auth.helper;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;

/**
 * 多账号体系：管理端与 C 端登录 ID 可能重叠，使用不同 loginType 隔离。
 */
public final class StpKit {

    /**
     * Admin 会话对象，管理 Admin 表所有账号的登录、权限认证
     * <p>同时作为 {@code StpUtil} 默认逻辑，供 {@code @SaCheckPermission} 使用
     */
    public static final StpLogic ADMIN = new StpLogicJwtForSimple("admin");

    /**
     * User 会话对象，管理 User 表所有账号的登录、权限认证
     */
    public static final StpLogic USER = new StpLogicJwtForSimple("user");

    private StpKit() {
    }
}
