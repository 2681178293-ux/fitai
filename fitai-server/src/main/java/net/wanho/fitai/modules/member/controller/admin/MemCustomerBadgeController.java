package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemCustomerBadgeAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemCustomerBadgeQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemCustomerBadgeUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemCustomerBadgeVO;
import net.wanho.fitai.modules.member.service.MemCustomerBadgeServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemCustomerBadgeController")
@RequestMapping("/admin/member/customer-badge")
@RequiredArgsConstructor
@Tag(name = "用户徽章接口")
public class MemCustomerBadgeController {

    private final MemCustomerBadgeServiceI memcustomerbadgeService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemCustomerBadgeVO>> findByPage(MemCustomerBadgeQueryDTO queryDto) {
        return AjaxResult.success(memcustomerbadgeService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemCustomerBadgeVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memcustomerbadgeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户徽章")
    public AjaxResult<Void> add(@Valid @RequestBody MemCustomerBadgeAddDTO addDto) {
        memcustomerbadgeService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户徽章")
    public AjaxResult<Void> update(@Valid @RequestBody MemCustomerBadgeUpdateDTO updateDto) {
        memcustomerbadgeService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户徽章")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memcustomerbadgeService.delete(id);
        return AjaxResult.success();
    }

}
