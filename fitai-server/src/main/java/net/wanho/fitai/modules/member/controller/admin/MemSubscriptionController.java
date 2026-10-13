package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemSubscriptionAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemSubscriptionQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemSubscriptionUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemSubscriptionVO;
import net.wanho.fitai.modules.member.service.MemSubscriptionServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemSubscriptionController")
@RequestMapping("/admin/member/subscription")
@RequiredArgsConstructor
@Tag(name = "用户会员订阅接口")
public class MemSubscriptionController {

    private final MemSubscriptionServiceI memsubscriptionService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemSubscriptionVO>> findByPage(MemSubscriptionQueryDTO queryDto) {
        return AjaxResult.success(memsubscriptionService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemSubscriptionVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(memsubscriptionService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户会员订阅")
    public AjaxResult<Void> add(@Valid @RequestBody MemSubscriptionAddDTO addDto) {
        memsubscriptionService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户会员订阅")
    public AjaxResult<Void> update(@Valid @RequestBody MemSubscriptionUpdateDTO updateDto) {
        memsubscriptionService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户会员订阅")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        memsubscriptionService.delete(id);
        return AjaxResult.success();
    }

}
