package net.wanho.fitai.infra.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

public class AddressUtil {

    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getIpLocation(String ip){
        String url ="https://restapi.amap.com/v3/ip?ip="+ip+"&output=json&key=103f48d44303bfa553b4c5780779c0d4";
        RestTemplate restTemplate = new RestTemplate();
        Map map = restTemplate.getForObject(url, Map.class);
        Object city = map.get("city");
        if(city instanceof ArrayList){
            return "局域网";
        }else{
            return map.get("province")+" "+map.get("city");
        }

    }
}
