package net.wanho.fitai.modules.course.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.course.model.dto.CrsLearnProgressAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsLearnProgressQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsLearnProgressUpdateDTO;
import net.wanho.fitai.modules.course.model.vo.CrsLearnProgressStatVO;
import net.wanho.fitai.modules.course.model.vo.CrsLearnProgressVO;
import net.wanho.fitai.modules.course.service.CrsLearnProgressServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminCrsLearnProgressController")
@RequestMapping("/admin/course/progress")
@RequiredArgsConstructor
@Tag(name = "课程学习进度接口")
public class CrsLearnProgressController {

    private final CrsLearnProgressServiceI crslearnprogressService;

    @GetMapping("/course/{courseId}/stats")
    public AjaxResult<CrsLearnProgressStatVO> stats(@PathVariable("courseId") Long courseId) {
        return AjaxResult.success(crslearnprogressService.stats(courseId));
    }

    @GetMapping("/course/{courseId}/students")
    @Operation(summary = "学员列表")
    public AjaxResult<List<CrsLearnProgressVO>> findStudentsByCourseId(@PathVariable("courseId") Long courseId) {
        return AjaxResult.success(crslearnprogressService.findStudentsByCourseId(courseId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CrsLearnProgressVO>> findByPage(CrsLearnProgressQueryDTO queryDto) {
        return AjaxResult.success(crslearnprogressService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CrsLearnProgressVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(crslearnprogressService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加课程学习进度")
    public AjaxResult<Void> add(@Valid @RequestBody CrsLearnProgressAddDTO addDto) {
        crslearnprogressService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改课程学习进度")
    public AjaxResult<Void> update(@Valid @RequestBody CrsLearnProgressUpdateDTO updateDto) {
        crslearnprogressService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除课程学习进度")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        crslearnprogressService.delete(id);
        return AjaxResult.success();
    }

}
