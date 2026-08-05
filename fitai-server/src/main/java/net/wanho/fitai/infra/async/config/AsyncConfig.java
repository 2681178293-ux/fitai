package net.wanho.fitai.infra.async.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置：启用 {@code @Async}，并声明具名线程池供业务指定。
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /** 登录日志异步写入线程池 Bean 名称（与 {@code @Async} 的 value 对应） */
    public static final String LOGIN_LOG_EXECUTOR = "loginLogExecutor";

    @Bean(name = LOGIN_LOG_EXECUTOR)
    public Executor loginLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("login-log-");
        // 队列满时由提交线程自己跑，避免直接丢弃登录日志
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
