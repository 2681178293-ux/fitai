package net.wanho.fitai.modules.course.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.course.model.dto.CrsCourseAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseUpdateDTO;
import net.wanho.fitai.modules.course.model.vo.CrsCourseVO;
import net.wanho.fitai.modules.course.service.CrsCourseServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCrsCourseController")
@RequestMapping("/admin/course")
@RequiredArgsConstructor
@Tag(name = "课程接口")
public class CrsCourseController {

    private final CrsCourseServiceI crscourseService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CrsCourseVO>> findByPage(CrsCourseQueryDTO queryDto) {
        return AjaxResult.success(crscourseService.findByPage(queryDto));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加课程")
    public AjaxResult<Void> add(@Valid @RequestBody CrsCourseAddDTO addDto) {
        crscourseService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改课程")
    public AjaxResult<Void> update(@Valid @RequestBody CrsCourseUpdateDTO updateDto) {
        crscourseService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除课程")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        crscourseService.delete(id);
        return AjaxResult.success();
    }


    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CrsCourseVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(crscourseService.findById(id));
    }
}
