package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemGrowthLogAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemGrowthLogQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemGrowthLogUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemGrowthLogVO;
import net.wanho.fitai.modules.member.service.MemGrowthLogServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemGrowthLogController")
@RequestMapping("/admin/member/growth-log")
@RequiredArgsConstructor
@Tag(name = "成长值流水接口")
public class MemGrowthLogController {

    private final MemGrowthLogServiceI memgrowthlogService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemGrowthLogVO>> findByPage(MemGrowthLogQueryDTO queryDto) {
        return AjaxResult.success(memgrowthlogService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemGrowthLogVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memgrowthlogService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加成长值流水")
    public AjaxResult<Void> add(@Valid @RequestBody MemGrowthLogAddDTO addDto) {
        memgrowthlogService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改成长值流水")
    public AjaxResult<Void> update(@Valid @RequestBody MemGrowthLogUpdateDTO updateDto) {
        memgrowthlogService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除成长值流水")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memgrowthlogService.delete(id);
        return AjaxResult.success();
    }

}
