package net.wanho.fitai.infra.util;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;

public class ServletUtil {

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes;
    }

    public static HttpServletRequest getRequest(){
        HttpServletRequest request = getRequestAttributes().getRequest();
        return request;
    }

    public static HttpServletResponse getResponse(){
        HttpServletResponse response = getRequestAttributes().getResponse();
        return response;
    }

    @SneakyThrows
    public static void writeJson(Object obj){
        HttpServletResponse response = getResponse();
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(JSONUtil.toJsonStr(obj));
    }
}
