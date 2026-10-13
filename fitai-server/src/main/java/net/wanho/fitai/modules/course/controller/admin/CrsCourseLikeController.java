package net.wanho.fitai.modules.course.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.course.model.dto.CrsCourseLikeAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseLikeQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseLikeUpdateDTO;
import net.wanho.fitai.modules.course.model.vo.CrsCourseLikeVO;
import net.wanho.fitai.modules.course.service.CrsCourseLikeServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCrsCourseLikeController")
@RequestMapping("/admin/course/course-like")
@RequiredArgsConstructor
@Tag(name = "课程点赞接口")
public class CrsCourseLikeController {

    private final CrsCourseLikeServiceI crscourselikeService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CrsCourseLikeVO>> findByPage(CrsCourseLikeQueryDTO queryDto) {
        return AjaxResult.success(crscourselikeService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CrsCourseLikeVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(crscourselikeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加课程点赞")
    public AjaxResult<Void> add(@Valid @RequestBody CrsCourseLikeAddDTO addDto) {
        crscourselikeService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改课程点赞")
    public AjaxResult<Void> update(@Valid @RequestBody CrsCourseLikeUpdateDTO updateDto) {
        crscourselikeService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除课程点赞")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        crscourselikeService.delete(id);
        return AjaxResult.success();
    }

}
