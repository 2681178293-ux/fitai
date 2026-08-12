package net.wanho.fitai.modules.system.log;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.auth.helper.AdminLoginHelper;
import net.wanho.fitai.infra.util.AddressUtil;
import net.wanho.fitai.infra.util.ServletUtil;
import net.wanho.fitai.modules.system.model.po.SysOperLog;
import net.wanho.fitai.modules.system.service.SysOperLogServiceI;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@RequiredArgsConstructor
public class OperLogAspect {

    private final SysOperLogServiceI sysOperLogService;

    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperLog operLog) throws Throwable {

        String name = joinPoint.getSignature().getName();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String method=signature.getDeclaringTypeName()+"."+signature.getName()+"()";

        String args = Arrays.toString(joinPoint.getArgs());
        HttpServletRequest request = ServletUtil.getRequest();
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();

        Integer businessType = 0;
        switch (requestMethod.toLowerCase()) {
            case "post":
                businessType = 1;
                break;
            case "put":
                businessType = 2;
                break;
            case "delete":
                businessType = 3;
                break;
        }

        SysOperLog sysOperLog = new SysOperLog();
        sysOperLog.setTitle(operLog.title());
        sysOperLog.setMethod(method);
        sysOperLog.setBusinessType(businessType);
        sysOperLog.setRequestMethod(requestMethod);
        sysOperLog.setOperName(AdminLoginHelper.getUsername());
        sysOperLog.setOperUrl(requestURI);
        sysOperLog.setOperIp(AddressUtil.getClientIP(request));
        sysOperLog.setOperParam(args);

        Long costTime = 0L;
        Object result = null;
        try {
            Long beginTime = System.currentTimeMillis();
            // 核心功能
            result = joinPoint.proceed();
            costTime = System.currentTimeMillis() - beginTime;
            sysOperLog.setJsonResult(JSONUtil.toJsonStr(result));
            sysOperLog.setStatus("0");
        } catch (Exception e) {
            sysOperLog.setStatus("1");
            sysOperLog.setErrorMsg(e.getMessage());
        }
        sysOperLog.setCostTime(costTime);
        sysOperLogService.recordOperLog(sysOperLog);
        return result;
    }
}
