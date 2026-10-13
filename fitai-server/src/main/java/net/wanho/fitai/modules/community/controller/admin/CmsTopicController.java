package net.wanho.fitai.modules.community.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.community.model.dto.CmsTopicAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsTopicQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsTopicUpdateDTO;
import net.wanho.fitai.modules.community.model.vo.CmsTopicVO;
import net.wanho.fitai.modules.community.service.CmsTopicServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminCmsTopicController")
@RequestMapping("/admin/community/topic")
@RequiredArgsConstructor
@Tag(name = "社区话题接口")
public class CmsTopicController {

    private final CmsTopicServiceI cmstopicService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<CmsTopicVO>> findByPage(CmsTopicQueryDTO queryDto) {
        return AjaxResult.success(cmstopicService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<CmsTopicVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(cmstopicService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加社区话题")
    public AjaxResult<Void> add(@Valid @RequestBody CmsTopicAddDTO addDto) {
        cmstopicService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改社区话题")
    public AjaxResult<Void> update(@Valid @RequestBody CmsTopicUpdateDTO updateDto) {
        cmstopicService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除社区话题")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        cmstopicService.delete(id);
        return AjaxResult.success();
    }

}
