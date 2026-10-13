package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiQuotaGiftAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiQuotaGiftQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiQuotaGiftUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiQuotaGiftVO;
import net.wanho.fitai.modules.ai.service.AiQuotaGiftServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiQuotaGiftController")
@RequestMapping("/admin/ai/quota-gift")
@RequiredArgsConstructor
@Tag(name = "AI配额赠送流水接口")
public class AiQuotaGiftController {

    private final AiQuotaGiftServiceI aiquotagiftService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiQuotaGiftVO>> findByPage(AiQuotaGiftQueryDTO queryDto) {
        return AjaxResult.success(aiquotagiftService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiQuotaGiftVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aiquotagiftService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI配额赠送流水")
    public AjaxResult<Void> add(@Valid @RequestBody AiQuotaGiftAddDTO addDto) {
        aiquotagiftService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI配额赠送流水")
    public AjaxResult<Void> update(@Valid @RequestBody AiQuotaGiftUpdateDTO updateDto) {
        aiquotagiftService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI配额赠送流水")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aiquotagiftService.delete(id);
        return AjaxResult.success();
    }

}
