package net.wanho.fitai.modules.course.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.course.model.dto.CrsCategoryAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCategoryUpdateDTO;
import net.wanho.fitai.modules.course.model.vo.CrsCategoryVO;
import net.wanho.fitai.modules.course.service.CrsCategoryServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminCrsCategoryController")
@RequestMapping("/admin/course/category")
@RequiredArgsConstructor
@Tag(name = "课程分类接口")
public class CrsCategoryController {

    private final CrsCategoryServiceI crscategoryService;

    @GetMapping("/tree")
    @Operation(summary = "分页查询")
    public AjaxResult<List<CrsCategoryVO>> tree() {
        return AjaxResult.success(crscategoryService.tree());
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加课程分类")
    public AjaxResult<Void> add( @RequestBody CrsCategoryAddDTO addDto) {
        crscategoryService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改课程分类")
    public AjaxResult<Void> update(@Valid @RequestBody CrsCategoryUpdateDTO updateDto) {
        crscategoryService.update(updateDto);
        return AjaxResult.success();
    }






    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CrsCategoryVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(crscategoryService.findById(id));
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除课程分类")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        crscategoryService.delete(id);
        return AjaxResult.success();
    }

}
