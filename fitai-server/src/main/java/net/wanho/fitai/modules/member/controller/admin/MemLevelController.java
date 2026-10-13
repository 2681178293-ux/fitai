package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.member.model.dto.MemLevelAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemLevelUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemLevelVO;
import net.wanho.fitai.modules.member.service.MemLevelServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemLevelController")
@RequestMapping("/admin/member/level")
@RequiredArgsConstructor
@Tag(name = "会员等级接口")
public class MemLevelController {

    private final MemLevelServiceI memlevelService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemLevelVO>> findByPage() {
        return AjaxResult.success(memlevelService.findByPage());
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemLevelVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memlevelService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加会员等级")
    public AjaxResult<Void> add(@Valid @RequestBody MemLevelAddDTO addDto) {
        memlevelService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改会员等级")
    public AjaxResult<Void> update(@Valid @RequestBody MemLevelUpdateDTO updateDto) {
        memlevelService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除会员等级")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memlevelService.delete(id);
        return AjaxResult.success();
    }

}
