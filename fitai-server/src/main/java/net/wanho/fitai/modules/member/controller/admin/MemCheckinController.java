package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemCheckinAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemCheckinQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemCheckinUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemCheckinVO;
import net.wanho.fitai.modules.member.service.MemCheckinServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemCheckinController")
@RequestMapping("/admin/member/checkin")
@RequiredArgsConstructor
@Tag(name = "每日签到接口")
public class MemCheckinController {

    private final MemCheckinServiceI memcheckinService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemCheckinVO>> findByPage(MemCheckinQueryDTO queryDto) {
        return AjaxResult.success(memcheckinService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemCheckinVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memcheckinService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加每日签到")
    public AjaxResult<Void> add(@Valid @RequestBody MemCheckinAddDTO addDto) {
        memcheckinService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改每日签到")
    public AjaxResult<Void> update(@Valid @RequestBody MemCheckinUpdateDTO updateDto) {
        memcheckinService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除每日签到")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memcheckinService.delete(id);
        return AjaxResult.success();
    }

}
