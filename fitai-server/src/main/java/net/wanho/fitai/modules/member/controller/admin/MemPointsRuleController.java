package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemPointsRuleAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsRuleQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsRuleUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemPointsRuleVO;
import net.wanho.fitai.modules.member.service.MemPointsRuleServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemPointsRuleController")
@RequestMapping("/admin/member/points-rule")
@RequiredArgsConstructor
@Tag(name = "积分规则接口")
public class MemPointsRuleController {

    private final MemPointsRuleServiceI mempointsruleService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemPointsRuleVO>> findByPage(MemPointsRuleQueryDTO queryDto) {
        return AjaxResult.success(mempointsruleService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemPointsRuleVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mempointsruleService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加积分规则")
    public AjaxResult<Void> add(@Valid @RequestBody MemPointsRuleAddDTO addDto) {
        mempointsruleService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改积分规则")
    public AjaxResult<Void> update(@Valid @RequestBody MemPointsRuleUpdateDTO updateDto) {
        mempointsruleService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除积分规则")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mempointsruleService.delete(id);
        return AjaxResult.success();
    }

}
