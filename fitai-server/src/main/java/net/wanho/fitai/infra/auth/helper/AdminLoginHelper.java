package net.wanho.fitai.infra.auth.helper;

import cn.dev33.satoken.session.SaSession;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import net.wanho.fitai.infra.auth.model.LoginUser;

import java.util.Map;

/**
 * 管理端登录信息工具：从 Session 读取登录缓存，减少重复查库。
 */
public final class AdminLoginHelper {

    private AdminLoginHelper() {
    }

    /** 获取当前登录缓存，未登录或未写入时返回 null */
    public static LoginUser getLoginUser() {
        if (!StpKit.ADMIN.isLogin()) {
            return null;
        }
        SaSession session = StpKit.ADMIN.getSession(false);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        Object value = session.get(SaSession.USER);
        if (value instanceof LoginUser loginUser) {
            return loginUser;
        }
        // Redis JSON 反序列化后可能是 Map
        if (value instanceof Map<?, ?> map) {
            return BeanUtil.toBean(map, LoginUser.class);
        }
        return null;
    }

    public static Long getUserId() {
        LoginUser user = getLoginUser();
        if (ObjectUtil.isNotNull(user) && ObjectUtil.isNotNull(user.getUserId())) {
            return user.getUserId();
        }
        return StpKit.ADMIN.isLogin() ? StpKit.ADMIN.getLoginIdAsLong() : null;
    }

    public static String getUsername() {
        LoginUser user = getLoginUser();
        return ObjectUtil.isNull(user) ? null : user.getUserName();
    }

    public static String getNickName() {
        LoginUser user = getLoginUser();
        return ObjectUtil.isNull(user) ? null : user.getNickName();
    }

    public static Long getDeptId() {
        LoginUser user = getLoginUser();
        return ObjectUtil.isNull(user) ? null : user.getDeptId();
    }

    public static String getDeptName() {
        LoginUser user = getLoginUser();
        return ObjectUtil.isNull(user) ? null : user.getDeptName();
    }
}
