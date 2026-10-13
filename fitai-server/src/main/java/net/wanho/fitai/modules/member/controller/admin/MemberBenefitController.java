package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemberBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemberBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemberBenefitVO;
import net.wanho.fitai.modules.member.service.MemberBenefitServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemberBenefitController")
@RequestMapping("/admin/member/member-benefit")
@RequiredArgsConstructor
@Tag(name = "会员权益表接口")
public class MemberBenefitController {

    private final MemberBenefitServiceI memberbenefitService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemberBenefitVO>> findByPage(MemberBenefitQueryDTO queryDto) {
        return AjaxResult.success(memberbenefitService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemberBenefitVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memberbenefitService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加会员权益表")
    public AjaxResult<Void> add(@Valid @RequestBody MemberBenefitAddDTO addDto) {
        memberbenefitService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改会员权益表")
    public AjaxResult<Void> update(@Valid @RequestBody MemberBenefitUpdateDTO updateDto) {
        memberbenefitService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除会员权益表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memberbenefitService.delete(id);
        return AjaxResult.success();
    }

}
