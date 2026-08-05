package net.wanho.fitai;

import cn.hutool.core.bean.BeanUtil;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import net.wanho.fitai.modules.system.model.dto.SysConfigAddDTO;
import net.wanho.fitai.modules.system.model.po.SysConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FitaiApplicationTests {

    @Resource
    private Converter converter;

    @Test
    void test1() {

        SysConfigAddDTO target = new SysConfigAddDTO();
        target.setConfigKey("aaa");
        target.setConfigValue("bbb");
        target.setConfigName("ccc");

        SysConfig convert = converter.convert(target, SysConfig.class);
        System.out.println(convert);


        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigName(target.getConfigName());
        sysConfig.setConfigKey(target.getConfigKey());
        sysConfig.setConfigValue(target.getConfigValue());
        System.out.println(sysConfig);

        SysConfig sysConfig2 = new SysConfig();
        BeanUtil.copyProperties(target, sysConfig2);
        System.out.println(sysConfig2);
    }

}
