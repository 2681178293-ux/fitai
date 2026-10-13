package net.wanho.fitai.modules.coach.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachScheduleAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachScheduleQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachScheduleUpdateDTO;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachScheduleVO;
import net.wanho.fitai.modules.coach.service.UsrCoachScheduleServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminUsrCoachScheduleController")
@RequestMapping("/admin/coach/coach-schedule")
@RequiredArgsConstructor
@Tag(name = "教练排课表(周模板+请假)接口")
public class UsrCoachScheduleController {

    private final UsrCoachScheduleServiceI usrcoachscheduleService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrCoachScheduleVO>> findByPage(UsrCoachScheduleQueryDTO queryDto) {
        return AjaxResult.success(usrcoachscheduleService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrCoachScheduleVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usrcoachscheduleService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加教练排课表(周模板+请假)")
    public AjaxResult<Void> add(@Valid @RequestBody UsrCoachScheduleAddDTO addDto) {
        usrcoachscheduleService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改教练排课表(周模板+请假)")
    public AjaxResult<Void> update(@Valid @RequestBody UsrCoachScheduleUpdateDTO updateDto) {
        usrcoachscheduleService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除教练排课表(周模板+请假)")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usrcoachscheduleService.delete(id);
        return AjaxResult.success();
    }

}
