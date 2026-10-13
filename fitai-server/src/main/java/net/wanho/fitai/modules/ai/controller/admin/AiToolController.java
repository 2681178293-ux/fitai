package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiToolAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiToolQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiToolUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiToolVO;
import net.wanho.fitai.modules.ai.service.AiToolServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiToolController")
@RequestMapping("/admin/ai/tool")
@RequiredArgsConstructor
@Tag(name = "AI工具管理接口")
public class AiToolController {

    private final AiToolServiceI aitoolService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiToolVO>> findByPage(AiToolQueryDTO queryDto) {
        return AjaxResult.success(aitoolService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiToolVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aitoolService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI工具管理")
    public AjaxResult<Void> add(@Valid @RequestBody AiToolAddDTO addDto) {
        aitoolService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI工具管理")
    public AjaxResult<Void> update(@Valid @RequestBody AiToolUpdateDTO updateDto) {
        aitoolService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI工具管理")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aitoolService.delete(id);
        return AjaxResult.success();
    }

}
