package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.UsrSegmentVO;
import net.wanho.fitai.modules.member.service.UsrSegmentServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminUsrSegmentController")
@RequestMapping("/admin/user/segment")
@RequiredArgsConstructor
@Tag(name = "用户分群接口")
public class UsrSegmentController {

    private final UsrSegmentServiceI usrsegmentService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrSegmentVO>> findByPage(UsrSegmentQueryDTO queryDto) {
        return AjaxResult.success(usrsegmentService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrSegmentVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usrsegmentService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户分群")
    public AjaxResult<Void> add(@Valid @RequestBody UsrSegmentAddDTO addDto) {
        usrsegmentService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户分群")
    public AjaxResult<Void> update(@Valid @RequestBody UsrSegmentUpdateDTO updateDto) {
        usrsegmentService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户分群")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usrsegmentService.delete(id);
        return AjaxResult.success();
    }

}
