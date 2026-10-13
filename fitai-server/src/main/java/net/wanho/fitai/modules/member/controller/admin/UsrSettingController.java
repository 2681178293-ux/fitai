package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.UsrSettingAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSettingQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSettingUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.UsrSettingVO;
import net.wanho.fitai.modules.member.service.UsrSettingServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminUsrSettingController")
@RequestMapping("/admin/user/setting")
@RequiredArgsConstructor
@Tag(name = "用户训练/饮食偏好设置接口")
public class UsrSettingController {

    private final UsrSettingServiceI usrsettingService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrSettingVO>> findByPage(UsrSettingQueryDTO queryDto) {
        return AjaxResult.success(usrsettingService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrSettingVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usrsettingService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户训练/饮食偏好设置")
    public AjaxResult<Void> add(@Valid @RequestBody UsrSettingAddDTO addDto) {
        usrsettingService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户训练/饮食偏好设置")
    public AjaxResult<Void> update(@Valid @RequestBody UsrSettingUpdateDTO updateDto) {
        usrsettingService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户训练/饮食偏好设置")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usrsettingService.delete(id);
        return AjaxResult.success();
    }

}
