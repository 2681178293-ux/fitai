package net.wanho.fitai.modules.course.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.course.model.dto.CrsVideoAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsVideoQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsVideoUpdateDTO;
import net.wanho.fitai.modules.course.model.vo.CrsVideoVO;
import net.wanho.fitai.modules.course.service.CrsVideoServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCrsVideoController")
@RequestMapping("/admin/course/video")
@RequiredArgsConstructor
@Tag(name = "课程视频接口")
public class CrsVideoController {

    private final CrsVideoServiceI crsvideoService;


    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CrsVideoVO>> findByPage(CrsVideoQueryDTO queryDto) {
        return AjaxResult.success(crsvideoService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CrsVideoVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(crsvideoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加课程视频")
    public AjaxResult<Void> add(@Valid @RequestBody CrsVideoAddDTO addDto) {
        crsvideoService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改课程视频")
    public AjaxResult<Void> update(@Valid @RequestBody CrsVideoUpdateDTO updateDto) {
        crsvideoService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除课程视频")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        crsvideoService.delete(id);
        return AjaxResult.success();
    }

}
