package net.wanho.fitai.infra.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 客户端路径重写
 * 背景:
 *      1、管理端的功能是以/admin开头的，本准备用户端直接以/开头
 *      2、但为了统计客户端的来源，需要区分，现在用户端以/pc或/mobile开头，但真正的后端接口还是/开头
 *  实现
 *      1、前端会有三种路径的前辍 /admin /pc /mobile
 *      2、根据前辍，将路径重写为 /admin/xxx 或 /xxx
 *      3、代码要简洁明了，一看就懂
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClientPathRewriterTimingFilter extends OncePerRequestFilter {

    private static final String PC_PREFIX = "/pc";
    private static final String MOBILE_PREFIX = "/mobile";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 获取路径
        String servletPath = request.getServletPath();

        // /pc/xxx、/mobile/xxx → /xxx；/admin/xxx 保持不变
        String rewrittenPath = servletPath;
        if (servletPath.startsWith(PC_PREFIX)) {
            rewrittenPath = servletPath.replace(PC_PREFIX, "");
        } else if (servletPath.startsWith(MOBILE_PREFIX)) {
            rewrittenPath = servletPath.replace(MOBILE_PREFIX, "");
        }

        // 未改写（含 /admin）：直接放行
        if (servletPath.equals(rewrittenPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(new RewrittenPathRequestWrapper(request, rewrittenPath), response);
    }

    private static class RewrittenPathRequestWrapper extends HttpServletRequestWrapper {

        private final String rewrittenPath;

        RewrittenPathRequestWrapper(HttpServletRequest request, String rewrittenPath) {
            super(request);
            this.rewrittenPath = rewrittenPath;
        }

        @Override
        public String getServletPath() {
            return rewrittenPath;
        }

        @Override
        public String getRequestURI() {
            return getContextPath() + rewrittenPath;
        }
    }
}
