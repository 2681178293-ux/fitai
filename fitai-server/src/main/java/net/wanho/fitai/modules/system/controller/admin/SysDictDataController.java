package net.wanho.fitai.modules.system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.model.dto.SysDictDataAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictDataQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDictDataUpdateDTO;
import net.wanho.fitai.modules.system.model.vo.SysDictDataVO;
import net.wanho.fitai.modules.system.service.SysDictDataServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSysDictDataController")
@RequestMapping("/admin/system/dict/data")
@RequiredArgsConstructor
@Tag(name = "字典数据接口")
public class SysDictDataController {

    private final SysDictDataServiceI sysDictDataService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    @SaCheckPermission
    public AjaxResult<IPage<SysDictDataVO>> findByPage(SysDictDataQueryDTO queryDto) {
        return AjaxResult.success(sysDictDataService.findByPage(queryDto));
    }

    @GetMapping("/{dictCode}")
    @Operation(summary = "查询")
    @SaCheckPermission("system:dict:data:query")
    public AjaxResult<SysDictDataVO> findById(@PathVariable("dictCode") Long dictCode) {
        return AjaxResult.success(sysDictDataService.findById(dictCode));
    }

    @PostMapping
    @Operation(summary = "添加")
    @SaCheckPermission("system:dict:data:add")
    public AjaxResult<Void> add(@Valid @RequestBody SysDictDataAddDTO addDto) {
        sysDictDataService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @SaCheckPermission("system:dict:data:update")
    public AjaxResult<Void> update(@Valid @RequestBody SysDictDataUpdateDTO updateDto) {
        sysDictDataService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除")
    @SaCheckPermission("system:dict:data:delete")
    public AjaxResult<Void> delete(@PathVariable("ids") String ids) {
        sysDictDataService.delete(ids);
        return AjaxResult.success();
    }
}
