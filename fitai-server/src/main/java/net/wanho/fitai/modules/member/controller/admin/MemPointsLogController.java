package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemPointsLogAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsLogQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsLogUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemPointsLogVO;
import net.wanho.fitai.modules.member.service.MemPointsLogServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemPointsLogController")
@RequestMapping("/admin/member/points-log")
@RequiredArgsConstructor
@Tag(name = "积分流水接口")
public class MemPointsLogController {

    private final MemPointsLogServiceI mempointslogService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemPointsLogVO>> findByPage(MemPointsLogQueryDTO queryDto) {
        return AjaxResult.success(mempointslogService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemPointsLogVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mempointslogService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加积分流水")
    public AjaxResult<Void> add(@Valid @RequestBody MemPointsLogAddDTO addDto) {
        mempointslogService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改积分流水")
    public AjaxResult<Void> update(@Valid @RequestBody MemPointsLogUpdateDTO updateDto) {
        mempointslogService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除积分流水")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mempointslogService.delete(id);
        return AjaxResult.success();
    }

}
