package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysDeptAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDeptQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDeptUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.SysDeptVO;
import net.wanho.fitai.modules.system.service.SysDeptServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysDeptController")
@RequestMapping("/admin/system/dept")
@RequiredArgsConstructor
@Tag(name = "部门管理接口")
public class SysDeptController {

    private final SysDeptServiceI sysDeptService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:dept:list")
    public AjaxResult<IPage<SysDeptVO>> findByPage(SysDeptQueryDTO queryDto) {
        return AjaxResult.success(sysDeptService.findByPage(queryDto));
    }

    @GetMapping("/{deptId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:dept:query")
    public AjaxResult<SysDeptVO> findById(@PathVariable("deptId") Long deptId) {
        return AjaxResult.success(sysDeptService.findById(deptId));
    }

    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:dept:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysDeptAddDTO addDto) {
        sysDeptService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:dept:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysDeptUpdateDTO updateDto) {
        sysDeptService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:dept:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysDeptService.delete(ids);
        return AjaxResult.success();
    }
}
