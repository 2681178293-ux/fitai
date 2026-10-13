package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentRelAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentRelQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentRelUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.UsrSegmentRelVO;
import net.wanho.fitai.modules.member.service.UsrSegmentRelServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminUsrSegmentRelController")
@RequestMapping("/admin/user/segment-rel")
@RequiredArgsConstructor
@Tag(name = "用户分群关联接口")
public class UsrSegmentRelController {

    private final UsrSegmentRelServiceI usrsegmentrelService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrSegmentRelVO>> findByPage(UsrSegmentRelQueryDTO queryDto) {
        return AjaxResult.success(usrsegmentrelService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrSegmentRelVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usrsegmentrelService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户分群关联")
    public AjaxResult<Void> add(@Valid @RequestBody UsrSegmentRelAddDTO addDto) {
        usrsegmentrelService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户分群关联")
    public AjaxResult<Void> update(@Valid @RequestBody UsrSegmentRelUpdateDTO updateDto) {
        usrsegmentrelService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户分群关联")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usrsegmentrelService.delete(id);
        return AjaxResult.success();
    }

}
