package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiVectorIndexAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiVectorIndexQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiVectorIndexUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiVectorIndexVO;
import net.wanho.fitai.modules.ai.service.AiVectorIndexServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiVectorIndexController")
@RequestMapping("/admin/ai/vector-index")
@RequiredArgsConstructor
@Tag(name = "Redis向量索引注册接口")
public class AiVectorIndexController {

    private final AiVectorIndexServiceI aivectorindexService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiVectorIndexVO>> findByPage(AiVectorIndexQueryDTO queryDto) {
        return AjaxResult.success(aivectorindexService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiVectorIndexVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aivectorindexService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加Redis向量索引注册")
    public AjaxResult<Void> add(@Valid @RequestBody AiVectorIndexAddDTO addDto) {
        aivectorindexService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改Redis向量索引注册")
    public AjaxResult<Void> update(@Valid @RequestBody AiVectorIndexUpdateDTO updateDto) {
        aivectorindexService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除Redis向量索引注册")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aivectorindexService.delete(id);
        return AjaxResult.success();
    }

}
