package net.wanho.fitai.modules.community.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.community.model.dto.CmsPostAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostUpdateDTO;
import net.wanho.fitai.modules.community.model.vo.CmsPostVO;
import net.wanho.fitai.modules.community.service.CmsPostServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCmsPostController")
@RequestMapping("/admin/community/cms-post")
@RequiredArgsConstructor
@Tag(name = "社区动态接口")
public class CmsPostController {

    private final CmsPostServiceI cmspostService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CmsPostVO>> findByPage(CmsPostQueryDTO queryDto) {
        return AjaxResult.success(cmspostService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CmsPostVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(cmspostService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加社区动态")
    public AjaxResult<Void> add(@Valid @RequestBody CmsPostAddDTO addDto) {
        cmspostService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改社区动态")
    public AjaxResult<Void> update(@Valid @RequestBody CmsPostUpdateDTO updateDto) {
        cmspostService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除社区动态")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        cmspostService.delete(id);
        return AjaxResult.success();
    }

}
