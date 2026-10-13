package net.wanho.fitai;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;

public class MyTest {

    @Test
    public void test1() {
        String name = FileUtil.mainName("aa.jpg");
        System.out.println(name);
    }
}
