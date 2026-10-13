package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetRowAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetRowQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiDatasetRowUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiDatasetRowVO;
import net.wanho.fitai.modules.ai.service.AiDatasetRowServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiDatasetRowController")
@RequestMapping("/admin/ai/dataset-row")
@RequiredArgsConstructor
@Tag(name = "数据集行接口")
public class AiDatasetRowController {

    private final AiDatasetRowServiceI aidatasetrowService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiDatasetRowVO>> findByPage(AiDatasetRowQueryDTO queryDto) {
        return AjaxResult.success(aidatasetrowService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiDatasetRowVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aidatasetrowService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加数据集行")
    public AjaxResult<Void> add(@Valid @RequestBody AiDatasetRowAddDTO addDto) {
        aidatasetrowService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改数据集行")
    public AjaxResult<Void> update(@Valid @RequestBody AiDatasetRowUpdateDTO updateDto) {
        aidatasetrowService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除数据集行")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aidatasetrowService.delete(id);
        return AjaxResult.success();
    }

}
