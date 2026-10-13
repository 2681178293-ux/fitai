package net.wanho.fitai.modules.coach.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBookingAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBookingQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBookingUpdateDTO;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachBookingVO;
import net.wanho.fitai.modules.coach.service.UsrCoachBookingServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminUsrCoachBookingController")
@RequestMapping("/admin/coach/booking")
@RequiredArgsConstructor
@Tag(name = "教练约课预约单接口")
public class UsrCoachBookingController {

    private final UsrCoachBookingServiceI usrcoachbookingService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrCoachBookingVO>> findByPage(UsrCoachBookingQueryDTO queryDto) {
        return AjaxResult.success(usrcoachbookingService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrCoachBookingVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usrcoachbookingService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加教练约课预约单")
    public AjaxResult<Void> add(@Valid @RequestBody UsrCoachBookingAddDTO addDto) {
        usrcoachbookingService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改教练约课预约单")
    public AjaxResult<Void> update(@Valid @RequestBody UsrCoachBookingUpdateDTO updateDto) {
        usrcoachbookingService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除教练约课预约单")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usrcoachbookingService.delete(id);
        return AjaxResult.success();
    }

}
