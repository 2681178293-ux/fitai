package net.wanho.fitai.infra.auth.helper;

import cn.dev33.satoken.session.SaSession;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import net.wanho.fitai.infra.auth.model.LoginCustomer;

import java.util.Map;

/**
 * C 端登录信息工具：从 Session 读取登录缓存，减少重复查库。
 */
public final class UserLoginHelper {

    private UserLoginHelper() {
    }

    /** 登录后写入 Session */
    public static void setLoginCustomer(LoginCustomer loginCustomer) {
        StpKit.USER.getSession().set(SaSession.USER, loginCustomer);
    }

    /** 获取当前登录缓存，未登录或未写入时返回 null */
    public static LoginCustomer getLoginCustomer() {
        if (!StpKit.USER.isLogin()) {
            return null;
        }
        SaSession session = StpKit.USER.getSession(false);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        Object value = session.get(SaSession.USER);
        if (value instanceof LoginCustomer loginCustomer) {
            return loginCustomer;
        }
        // Redis JSON 反序列化后可能是 Map
        if (value instanceof Map<?, ?> map) {
            return BeanUtil.toBean(map, LoginCustomer.class);
        }
        return null;
    }

    public static Long getCustomerId() {
        LoginCustomer customer = getLoginCustomer();
        if (ObjectUtil.isNotNull(customer) && ObjectUtil.isNotNull(customer.getCustomerId())) {
            return customer.getCustomerId();
        }
        return StpKit.USER.isLogin() ? StpKit.USER.getLoginIdAsLong() : null;
    }

    public static String getNickName() {
        LoginCustomer customer = getLoginCustomer();
        return ObjectUtil.isNull(customer) ? null : customer.getNickName();
    }

    public static String getMobile() {
        LoginCustomer customer = getLoginCustomer();
        return ObjectUtil.isNull(customer) ? null : customer.getMobile();
    }
}
