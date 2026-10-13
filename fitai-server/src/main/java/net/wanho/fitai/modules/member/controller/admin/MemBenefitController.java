package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemBenefitAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemBenefitQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemBenefitUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemBenefitVO;
import net.wanho.fitai.modules.member.service.MemBenefitServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemBenefitController")
@RequestMapping("/admin/member/benefit")
@RequiredArgsConstructor
@Tag(name = "会员权益接口")
public class MemBenefitController {

    private final MemBenefitServiceI membenefitService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemBenefitVO>> findByPage(MemBenefitQueryDTO queryDto) {
        return AjaxResult.success(membenefitService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemBenefitVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(membenefitService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加会员权益")
    public AjaxResult<Void> add(@Valid @RequestBody MemBenefitAddDTO addDto) {
        membenefitService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改会员权益")
    public AjaxResult<Void> update(@Valid @RequestBody MemBenefitUpdateDTO updateDto) {
        membenefitService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除会员权益")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        membenefitService.delete(id);
        return AjaxResult.success();
    }

}
