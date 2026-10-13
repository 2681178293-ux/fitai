package net.wanho.fitai.modules.ai.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.ai.model.dto.AiUserQuotaAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiUserQuotaQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiUserQuotaUpdateDTO;
import net.wanho.fitai.modules.ai.model.vo.AiUserQuotaVO;
import net.wanho.fitai.modules.ai.service.AiUserQuotaServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAiUserQuotaController")
@RequestMapping("/admin/ai/user-quota")
@RequiredArgsConstructor
@Tag(name = "AI用户月额度接口")
public class AiUserQuotaController {

    private final AiUserQuotaServiceI aiuserquotaService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<AiUserQuotaVO>> findByPage(AiUserQuotaQueryDTO queryDto) {
        return AjaxResult.success(aiuserquotaService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<AiUserQuotaVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(aiuserquotaService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加AI用户月额度")
    public AjaxResult<Void> add(@Valid @RequestBody AiUserQuotaAddDTO addDto) {
        aiuserquotaService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改AI用户月额度")
    public AjaxResult<Void> update(@Valid @RequestBody AiUserQuotaUpdateDTO updateDto) {
        aiuserquotaService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除AI用户月额度")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        aiuserquotaService.delete(id);
        return AjaxResult.success();
    }

}
