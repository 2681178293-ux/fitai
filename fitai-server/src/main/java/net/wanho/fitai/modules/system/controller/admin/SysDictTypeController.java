package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysDictTypeAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictTypeQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictTypeUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.SysDictTypeVO;
import net.wanho.fitai.modules.system.service.SysDictTypeServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysDictTypeController")
@RequestMapping("/admin/system/dict/type")
@RequiredArgsConstructor
@Tag(name = "字典类型接口")
public class SysDictTypeController {

    private final SysDictTypeServiceI sysDictTypeService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission("system:dict:type:list")
    public AjaxResult<IPage<SysDictTypeVO>> findByPage(SysDictTypeQueryDTO queryDto) {
        return AjaxResult.success(sysDictTypeService.findByPage(queryDto));
    }

    @GetMapping("/{dictId}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:dict:type:query")
    public AjaxResult<SysDictTypeVO> findById(@PathVariable("dictId") Long dictId) {
        return AjaxResult.success(sysDictTypeService.findById(dictId));
    }

    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:dict:type:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysDictTypeAddDTO addDto) {
        sysDictTypeService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:dict:type:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysDictTypeUpdateDTO updateDto) {
        sysDictTypeService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:dict:type:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysDictTypeService.delete(ids);
        return AjaxResult.success();
    }
}
