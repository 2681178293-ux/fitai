package net.wanho.fitai.modules.member.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.member.model.dto.MemberPlanAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberPlanUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemberPlanVO;
import net.wanho.fitai.modules.member.service.MemberPlanServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminMemberPlanController")
@RequestMapping("/admin/member/plans")
@RequiredArgsConstructor
@Tag(name = "会员套餐表接口")
public class MemberPlanController {

    private final MemberPlanServiceI memberplanService;

    @GetMapping
    @Operation(summary = "分页查询")
    public AjaxResult<List<MemberPlanVO>> findByPage() {
        return AjaxResult.success(memberplanService.findByPage());
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemberPlanVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memberplanService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加会员套餐表")
    public AjaxResult<Void> add(@Valid @RequestBody MemberPlanAddDTO addDto) {
        memberplanService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改会员套餐表")
    public AjaxResult<Void> update(@Valid @RequestBody MemberPlanUpdateDTO updateDto) {
        memberplanService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除会员套餐表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memberplanService.delete(id);
        return AjaxResult.success();
    }

}
