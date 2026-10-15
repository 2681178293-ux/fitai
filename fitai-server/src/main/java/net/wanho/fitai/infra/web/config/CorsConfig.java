package net.wanho.fitai.infra.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置（非注解方式）。
 * <p>
 * 当前项目开发/生产通常走 Vite 或 nginx 反向代理，浏览器视角为同源，可不依赖本配置。
 * 当前端直连后端（如不同端口、不同域名）时，由本配置统一返回 CORS 响应头。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {


    @Value("${fitai.cors.allowed-origin-patterns:http://localhost:*,http://127.0.0.1:*}")
    private String[] allowedOriginPatterns;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
