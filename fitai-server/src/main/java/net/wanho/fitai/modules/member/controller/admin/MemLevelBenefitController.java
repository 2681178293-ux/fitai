package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemLevelBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemLevelBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemLevelBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemLevelBenefitVO;
import net.wanho.fitai.modules.member.service.MemLevelBenefitServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemLevelBenefitController")
@RequestMapping("/admin/member/mem-level-benefit")
@RequiredArgsConstructor
@Tag(name = "等级权益关联接口")
public class MemLevelBenefitController {

    private final MemLevelBenefitServiceI memlevelbenefitService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemLevelBenefitVO>> findByPage(MemLevelBenefitQueryDTO queryDto) {
        return AjaxResult.success(memlevelbenefitService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemLevelBenefitVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memlevelbenefitService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加等级权益关联")
    public AjaxResult<Void> add(@Valid @RequestBody MemLevelBenefitAddDTO addDto) {
        memlevelbenefitService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改等级权益关联")
    public AjaxResult<Void> update(@Valid @RequestBody MemLevelBenefitUpdateDTO updateDto) {
        memlevelbenefitService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除等级权益关联")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memlevelbenefitService.delete(id);
        return AjaxResult.success();
    }

}
