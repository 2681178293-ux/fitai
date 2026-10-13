package net.wanho.fitai.modules.community.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.community.model.dto.CommunityPostAddDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityPostQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityPostUpdateDTO;
import net.wanho.fitai.modules.community.model.vo.CommunityPostVO;
import net.wanho.fitai.modules.community.service.CommunityPostServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCommunityPostController")
@RequestMapping("/admin/community/post")
@RequiredArgsConstructor
@Tag(name = "社区动态表接口")
public class CommunityPostController {

    private final CommunityPostServiceI communitypostService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CommunityPostVO>> findByPage(CommunityPostQueryDTO queryDto) {
        return AjaxResult.success(communitypostService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CommunityPostVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(communitypostService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加社区动态表")
    public AjaxResult<Void> add(@Valid @RequestBody CommunityPostAddDTO addDto) {
        communitypostService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改社区动态表")
    public AjaxResult<Void> update(@Valid @RequestBody CommunityPostUpdateDTO updateDto) {
        communitypostService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除社区动态表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        communitypostService.delete(id);
        return AjaxResult.success();
    }

}
