package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemberLevelBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberLevelBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemberLevelBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemberLevelBenefitVO;
import net.wanho.fitai.modules.member.service.MemberLevelBenefitServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemberLevelBenefitController")
@RequestMapping("/admin/member/member-level-benefit")
@RequiredArgsConstructor
@Tag(name = "等级权益关联表接口")
public class MemberLevelBenefitController {

    private final MemberLevelBenefitServiceI memberlevelbenefitService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemberLevelBenefitVO>> findByPage(MemberLevelBenefitQueryDTO queryDto) {
        return AjaxResult.success(memberlevelbenefitService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemberLevelBenefitVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memberlevelbenefitService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加等级权益关联表")
    public AjaxResult<Void> add(@Valid @RequestBody MemberLevelBenefitAddDTO addDto) {
        memberlevelbenefitService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改等级权益关联表")
    public AjaxResult<Void> update(@Valid @RequestBody MemberLevelBenefitUpdateDTO updateDto) {
        memberlevelbenefitService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除等级权益关联表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memberlevelbenefitService.delete(id);
        return AjaxResult.success();
    }

}
