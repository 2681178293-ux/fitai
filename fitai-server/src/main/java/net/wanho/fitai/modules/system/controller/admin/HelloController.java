package net.wanho.fitai.modules.system.controller.admin;

import net.wanho.fitai.infra.result.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public AjaxResult<String> hello(){
        return AjaxResult.success("hello 152");
    }
}
