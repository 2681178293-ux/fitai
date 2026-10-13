package net.wanho.fitai.modules.diet.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.diet.model.dto.DietRecordAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietRecordQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietRecordUpdateDTO;
import net.wanho.fitai.modules.diet.model.vo.DietRecordVO;
import net.wanho.fitai.modules.diet.service.DietRecordServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminDietRecordController")
@RequestMapping("/admin/diet/record")
@RequiredArgsConstructor
@Tag(name = "饮食记录接口")
public class DietRecordController {

    private final DietRecordServiceI dietrecordService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<DietRecordVO>> findByPage(DietRecordQueryDTO queryDto) {
        return AjaxResult.success(dietrecordService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<DietRecordVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(dietrecordService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加饮食记录")
    public AjaxResult<Void> add(@Valid @RequestBody DietRecordAddDTO addDto) {
        dietrecordService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改饮食记录")
    public AjaxResult<Void> update(@Valid @RequestBody DietRecordUpdateDTO updateDto) {
        dietrecordService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除饮食记录")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        dietrecordService.delete(id);
        return AjaxResult.success();
    }

}
