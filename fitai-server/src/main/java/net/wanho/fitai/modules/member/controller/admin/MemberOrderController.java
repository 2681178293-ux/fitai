package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemberOrderAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberOrderQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemberOrderUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemberOrderVO;
import net.wanho.fitai.modules.member.service.MemberOrderServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemberOrderController")
@RequestMapping("/admin/member/order")
@RequiredArgsConstructor
@Tag(name = "会员订单表接口")
public class MemberOrderController {

    private final MemberOrderServiceI memberorderService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemberOrderVO>> findByPage(MemberOrderQueryDTO queryDto) {
        return AjaxResult.success(memberorderService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemberOrderVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memberorderService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加会员订单表")
    public AjaxResult<Void> add(@Valid @RequestBody MemberOrderAddDTO addDto) {
        memberorderService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改会员订单表")
    public AjaxResult<Void> update(@Valid @RequestBody MemberOrderUpdateDTO updateDto) {
        memberorderService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除会员订单表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memberorderService.delete(id);
        return AjaxResult.success();
    }

}
