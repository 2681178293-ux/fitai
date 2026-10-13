package net.wanho.fitai.modules.coach.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachUpdateDTO;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachVO;
import net.wanho.fitai.modules.coach.service.UsrCoachServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminUsrCoachController")
@RequestMapping("/admin/coach")
@RequiredArgsConstructor
@Tag(name = "教练接口")
public class UsrCoachController {

    private final UsrCoachServiceI usrcoachService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrCoachVO>> findByPage(UsrCoachQueryDTO queryDto) {
        return AjaxResult.success(usrcoachService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrCoachVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usrcoachService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加教练")
    public AjaxResult<Void> add(@Valid @RequestBody UsrCoachAddDTO addDto) {
        usrcoachService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改教练")
    public AjaxResult<Void> update(@Valid @RequestBody UsrCoachUpdateDTO updateDto) {
        usrcoachService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除教练")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usrcoachService.delete(id);
        return AjaxResult.success();
    }

}
