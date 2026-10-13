package net.wanho.fitai.modules.community.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.community.model.dto.CmsModerationAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsModerationQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsModerationUpdateDTO;
import net.wanho.fitai.modules.community.model.vo.CmsModerationVO;
import net.wanho.fitai.modules.community.service.CmsModerationServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCmsModerationController")
@RequestMapping("/admin/community/cms-moderation")
@RequiredArgsConstructor
@Tag(name = "内容审核接口")
public class CmsModerationController {

    private final CmsModerationServiceI cmsmoderationService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CmsModerationVO>> findByPage(CmsModerationQueryDTO queryDto) {
        return AjaxResult.success(cmsmoderationService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CmsModerationVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(cmsmoderationService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加内容审核")
    public AjaxResult<Void> add(@Valid @RequestBody CmsModerationAddDTO addDto) {
        cmsmoderationService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改内容审核")
    public AjaxResult<Void> update(@Valid @RequestBody CmsModerationUpdateDTO updateDto) {
        cmsmoderationService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除内容审核")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        cmsmoderationService.delete(id);
        return AjaxResult.success();
    }

}
