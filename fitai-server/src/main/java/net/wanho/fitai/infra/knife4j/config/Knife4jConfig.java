package net.wanho.fitai.infra.knife4j.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 接口文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FitAI 智能健身平台 API")
                        .description("FitAI 后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FitAI Team")
                                .email("dev@fitai.com")));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户中心")
                .pathsToMatch("/user/**", "/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi trainingApi() {
        return GroupedOpenApi.builder()
                .group("AI训练")
                .pathsToMatch("/exercise/**", "/training/**", "/ai/**")
                .build();
    }

    @Bean
    public GroupedOpenApi courseApi() {
        return GroupedOpenApi.builder()
                .group("视频课程")
                .pathsToMatch("/course/**", "/video/**")
                .build();
    }

    @Bean
    public GroupedOpenApi mallApi() {
        return GroupedOpenApi.builder()
                .group("电商")
                .pathsToMatch("/product/**", "/cart/**", "/order/**", "/seckill/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("后台管理")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi sysApi() {
        return GroupedOpenApi.builder()
                .group("系统管理")
                .pathsToMatch("/sys/**")
                .build();
    }
}
