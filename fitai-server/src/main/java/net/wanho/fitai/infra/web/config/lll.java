package net.wanho.fitai.infra.web.config;


import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

public class lll {

    public static final DefaultRedisScript<Long> SECKILL_SCRIPT;;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setResultType(Long.class);
        // 读取resources/lua/aaa.lua
        SECKILL_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/aaa.lua")));
    }
}
