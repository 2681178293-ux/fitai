package net.wanho.fitai.modules.coach.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBindAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBindQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBindUpdateDTO;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachBindVO;
import net.wanho.fitai.modules.coach.service.UsrCoachBindServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminUsrCoachBindController")
@RequestMapping("/admin/coach/bind")
@RequiredArgsConstructor
@Tag(name = "用户教练绑定接口")
public class UsrCoachBindController {

    private final UsrCoachBindServiceI usrcoachbindService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrCoachBindVO>> findByPage(UsrCoachBindQueryDTO queryDto) {
        return AjaxResult.success(usrcoachbindService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrCoachBindVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usrcoachbindService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户教练绑定")
    public AjaxResult<Void> add(@Valid @RequestBody UsrCoachBindAddDTO addDto) {
        usrcoachbindService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户教练绑定")
    public AjaxResult<Void> update(@Valid @RequestBody UsrCoachBindUpdateDTO updateDto) {
        usrcoachbindService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户教练绑定")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usrcoachbindService.delete(id);
        return AjaxResult.success();
    }

}
