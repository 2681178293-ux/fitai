package net.wanho.fitai.modules.system.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wanho.fitai.infra.async.config.AsyncConfig;
import net.wanho.fitai.modules.system.mapper.SysLoginLogMapper;
import net.wanho.fitai.modules.system.model.po.SysLoginLog;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 登录日志异步落库。
 * <p>单独拆 Bean，避免在 {@code SysLoginLogServiceImpl} 同类内自调用导致 {@code @Async} 不生效。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginLogAsyncWriter {

    private final SysLoginLogMapper sysLoginLogMapper;

    @Async(AsyncConfig.LOGIN_LOG_EXECUTOR)
    public void write(SysLoginLog loginLog) {
        try {
            sysLoginLogMapper.insert(loginLog);
        } catch (Exception e) {
            // 日志失败不影响登录主流程
            log.error("异步写入登录日志失败, userName={}, msg={}", loginLog.getUserName(), loginLog.getMsg(), e);
        }
    }
}
