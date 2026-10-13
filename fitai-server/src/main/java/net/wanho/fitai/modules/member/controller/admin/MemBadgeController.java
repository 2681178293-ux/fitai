package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemBadgeAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemBadgeQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemBadgeUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemBadgeVO;
import net.wanho.fitai.modules.member.service.MemBadgeServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemBadgeController")
@RequestMapping("/admin/member/badge")
@RequiredArgsConstructor
@Tag(name = "徽章定义接口")
public class MemBadgeController {

    private final MemBadgeServiceI membadgeService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemBadgeVO>> findByPage(MemBadgeQueryDTO queryDto) {
        return AjaxResult.success(membadgeService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemBadgeVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(membadgeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加徽章定义")
    public AjaxResult<Void> add(@Valid @RequestBody MemBadgeAddDTO addDto) {
        membadgeService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改徽章定义")
    public AjaxResult<Void> update(@Valid @RequestBody MemBadgeUpdateDTO updateDto) {
        membadgeService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除徽章定义")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        membadgeService.delete(id);
        return AjaxResult.success();
    }

}
